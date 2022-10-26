package com.mauvaisetroupe.eadesignit.service.plantuml;

import com.mauvaisetroupe.eadesignit.domain.Capability;
import com.mauvaisetroupe.eadesignit.domain.FunctionalFlow;
import com.mauvaisetroupe.eadesignit.domain.LandscapeView;
import com.mauvaisetroupe.eadesignit.repository.view.FlowInterfaceLight;
import com.mauvaisetroupe.eadesignit.service.drawio.GraphBuilder;
import com.mauvaisetroupe.eadesignit.service.drawio.dto.Application;
import com.mauvaisetroupe.eadesignit.service.drawio.dto.Edge;
import com.mauvaisetroupe.eadesignit.service.drawio.dto.GraphDTO;
import com.mauvaisetroupe.eadesignit.service.importfile.dto.CapabilityDTO;
import com.mauvaisetroupe.eadesignit.service.importfile.util.CapabilityUtil;
import com.mauvaisetroupe.eadesignit.service.plantuml.PlantUMLBuilder.Layout;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlantUMLSerializer {

    private final Logger log = LoggerFactory.getLogger(PlantUMLSerializer.class);

    public enum DiagramType {
        COMPONENT_DIAGRAM,
        SEQUENCE_DIAGRAM,
    }

    public enum ShowSubComponent {
        KEEP_DEFAULT,
        FORCE_SHOW,
        FORCE_HIDE,
    }

    public enum GroupSubComponent {
        GROUP,
        DO_NOT_GROUP,
    }

    @Autowired
    private PlantUMLBuilder plantUMLBuilder;

    private String createPlantUMLSource(GraphDTO graph, DiagramType diagramType, boolean addURL, Layout layout, boolean groupComponents) {
        StringBuilder plantUMLSource = new StringBuilder();
        plantUMLBuilder.getPlantumlHeader(plantUMLSource, layout);
        boolean useID = false;
        if (addURL) {
            // Declare application without the ones included in packages
            // Will be declared in package, including url declaretaion
            for (Application application : groupComponents ? graph.getApplicationsWithoutGroups() : graph.getApplications()) {
                // itś not possible to add an URL for an Application or an ApplicationComponent inside a reliation [A] -> [B]
                // so we need to create a component for each Application / ApplicationComponent
                // and associate the URL to that componnet
                plantUMLBuilder.createComponentWithId(plantUMLSource, application, diagramType);
                useID = true;
            }
        }
        if (groupComponents) {
            // crerate groups (packages)
            for (Entry<String, List<Application>> groupEntry : graph.getGroups().entrySet()) {
                plantUMLBuilder.getPlantumlPackage(plantUMLSource, groupEntry.getKey(), groupEntry.getValue(), useID);
            }
        }

        for (Edge edge : graph.getConsolidatedEdges()) {
            Application source = graph.getApplication(edge.getSourceId());
            Application target = graph.getApplication(edge.getTargetId());
            plantUMLBuilder.getPlantumlRelationShip(plantUMLSource, source, target, edge.getLabels(), diagramType, useID);
        }
        plantUMLBuilder.getPlantumlFooter(plantUMLSource);
        System.out.println(plantUMLSource.toString());
        return plantUMLSource.toString();
    }

    public String getLandscapeDiagramSVG(LandscapeView landscapeView, Layout layout, boolean groupComponents) throws IOException {
        GraphBuilder graphBuilder = new GraphBuilder();
        GraphDTO graph = graphBuilder.createGraph(landscapeView);
        String plantUMLSource = createPlantUMLSource(graph, DiagramType.COMPONENT_DIAGRAM, true, layout, groupComponents);
        return plantUMLBuilder.getSVGFromSource(plantUMLSource.toString());
    }

    public String getLandscapeDiagramSource(LandscapeView landscapeView) throws IOException {
        GraphBuilder graphBuilder = new GraphBuilder();
        GraphDTO graph = graphBuilder.createGraph(landscapeView);
        return createPlantUMLSource(graph, DiagramType.COMPONENT_DIAGRAM, false, Layout.none, false);
    }

    public String getFunctionalFlowDiagramSVG(FunctionalFlow functionalFlow, DiagramType diagramType) throws IOException {
        GraphBuilder graphBuilder = new GraphBuilder();
        GraphDTO graph = graphBuilder.createGraph(functionalFlow);
        String plantUMLSource = createPlantUMLSource(graph, diagramType, true, Layout.smetana, false);
        return plantUMLBuilder.getSVGFromSource(plantUMLSource.toString());
    }

    public String getFunctionalFlowDiagramSource(FunctionalFlow functionalFlow, DiagramType diagramType) throws IOException {
        GraphBuilder graphBuilder = new GraphBuilder();
        GraphDTO graph = graphBuilder.createGraph(functionalFlow);
        return createPlantUMLSource(graph, diagramType, true, Layout.smetana, false);
    }

    public String getInterfacesCollectionDiagramSVG(SortedSet<FlowInterfaceLight> interfaces, Layout layout, boolean groupComponents)
        throws IOException {
        GraphBuilder graphBuilder = new GraphBuilder();
        GraphDTO graph = graphBuilder.createGraph(interfaces);
        String plantUMLSource = createPlantUMLSource(graph, DiagramType.COMPONENT_DIAGRAM, true, layout, groupComponents);
        return plantUMLBuilder.getSVGFromSource(plantUMLSource.toString());
    }

    public String getInterfacesCollectionDiagramSource(SortedSet<FlowInterfaceLight> interfaces) {
        GraphBuilder graphBuilder = new GraphBuilder();
        GraphDTO graph = graphBuilder.createGraph(interfaces);
        String plantUMLSource = createPlantUMLSource(graph, DiagramType.COMPONENT_DIAGRAM, false, Layout.none, false);
        return plantUMLSource.toString();
    }

    public String getCapabilitiesFromLeavesSVG(Collection<Capability> capabilities) throws IOException {
        StringBuilder plantUMLSource = new StringBuilder();
        plantUMLBuilder.getPlantumlHeader(plantUMLSource);

        CapabilityUtil capabilityUtil = new CapabilityUtil();
        Collection<CapabilityDTO> rootDTO = capabilityUtil.getRoot(capabilities);
        plantUMLBuilder.getPlantumlCapabilitiesDTO(plantUMLSource, rootDTO);
        plantUMLBuilder.getPlantumlFooter(plantUMLSource);
        System.out.println(plantUMLSource);
        return plantUMLBuilder.getSVGFromSource(plantUMLSource.toString());
    }

    public String getCapabilitiesFromRootsSVG(Collection<Capability> capabilities) throws IOException {
        StringBuilder plantUMLSource = new StringBuilder();
        plantUMLBuilder.getPlantumlHeader(plantUMLSource);
        plantUMLBuilder.getPlantumlCapabilities(plantUMLSource, capabilities);
        plantUMLBuilder.getPlantumlFooter(plantUMLSource);
        System.out.println(plantUMLSource);
        return plantUMLBuilder.getSVGFromSource(plantUMLSource.toString());
    }

    public String getSVGFromSource(String plantUMLSource) throws IOException {
        return plantUMLBuilder.getSVGFromSource(plantUMLSource);
    }
}
