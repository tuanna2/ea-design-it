package com.mauvaisetroupe.eadesignit.service.importfile;

import com.mauvaisetroupe.eadesignit.domain.Application;
import com.mauvaisetroupe.eadesignit.domain.Capability;
import com.mauvaisetroupe.eadesignit.repository.ApplicationRepository;
import com.mauvaisetroupe.eadesignit.repository.CapabilityRepository;
import com.mauvaisetroupe.eadesignit.service.importfile.dto.ApplicationCapabilityDTO;
import com.mauvaisetroupe.eadesignit.service.importfile.dto.CapabilityDTO;
import com.mauvaisetroupe.eadesignit.service.importfile.util.CapabilityUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.EncryptedDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationCapabilityImportService {

    private final Logger log = LoggerFactory.getLogger(ApplicationCapabilityImportService.class);

    @Autowired
    private CapabilityRepository capabilityRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private static final String APP_NAME_1 = "ApplicationName";
    private static final String APP_NAME_2 = "ApplicationName2";
    private static final String APP_NAME_3 = "ApplicationName3";
    private static final String APP_NAME_4 = "ApplicationName4";
    private static final String APP_NAME_5 = "ApplicationName5";

    private static final String L0_NAME = "CapabilityL0";
    private static final String L1_NAME = "CapabilityL1";
    private static final String L2_NAME = "CapabilityL2";
    private static final String L3_NAME = "CapabilityL3";

    public List<ApplicationCapabilityDTO> importExcel(InputStream excel, String originalFilename)
        throws EncryptedDocumentException, IOException {
        ExcelReader capabilityFlowExcelReader = new ExcelReader(excel);
        List<String> sheetnames = capabilityFlowExcelReader.getSheetNames();
        List<ApplicationCapabilityDTO> result = new ArrayList<ApplicationCapabilityDTO>();
        CapabilityUtil capabilityUtil = new CapabilityUtil();
        for (String sheetname : sheetnames) {
            if (sheetname.startsWith("ADD")) {
                List<Map<String, Object>> capabilitiesDF = capabilityFlowExcelReader.getSheet(sheetname);

                for (Map<String, Object> map : capabilitiesDF) {
                    ApplicationCapabilityDTO applicationCapabilityDTO = new ApplicationCapabilityDTO();

                    CapabilityDTO l0Import = capabilityUtil.mapArrayToCapability(map, L0_NAME, null, 0);
                    CapabilityDTO l1Import = capabilityUtil.mapArrayToCapability(map, L1_NAME, null, 1);
                    CapabilityDTO l2Import = capabilityUtil.mapArrayToCapability(map, L2_NAME, null, 2);
                    CapabilityDTO l3Import = capabilityUtil.mapArrayToCapability(map, L3_NAME, null, 3);
                    applicationCapabilityDTO.setCapabilityImportDTO(
                        capabilityUtil.mappArrayToCapabilityImport(l0Import, l1Import, l2Import, l3Import)
                    );

                    Capability capability = findCapability(l0Import, l1Import, l2Import, l3Import);
                    List<Application> applications = findApplication(map);
                    if (applications.isEmpty()) {
                        throw new IllegalStateException("No application found : " + map);
                    }
                    applicationCapabilityDTO.setApplication(applications);
                    for (Application application : applications) {
                        application.addCapabilities(capability);
                    }
                    result.add(applicationCapabilityDTO);
                }
            }
        }
        return result;
    }

    private List<Application> findApplication(Map<String, Object> map) {
        String[] apps = { APP_NAME_1, APP_NAME_2, APP_NAME_3, APP_NAME_4, APP_NAME_5 };
        List<Application> result = new ArrayList<>();
        for (String appName : apps) {
            String app1 = getApplicationName(map, appName);
            if (app1 != null) {
                Application application = applicationRepository.findByNameIgnoreCase(app1);
                if (application != null) {
                    result.add(application);
                } else {
                    log.error("Can not find application : [" + app1 + "]");
                }
            }
        }
        return result;
    }

    private String getApplicationName(Map<String, Object> map, String appName1) {
        Object obj = map.get(appName1);
        if (obj != null) {
            return obj.toString().trim();
        }
        return null;
    }

    private Capability findCapability(CapabilityDTO l0Import, CapabilityDTO l1Import, CapabilityDTO l2Import, CapabilityDTO l3Import) {
        List<Capability> potentials = null;
        if (l3Import != null) {
            potentials =
                capabilityRepository.findByNameIgnoreCaseAndParentNameIgnoreCaseAndLevel(l3Import.getName(), l2Import.getName(), 3);
        } else if (l2Import != null) {
            potentials =
                capabilityRepository.findByNameIgnoreCaseAndParentNameIgnoreCaseAndLevel(l2Import.getName(), l1Import.getName(), 2);
        } else if (l1Import != null) {
            potentials =
                capabilityRepository.findByNameIgnoreCaseAndParentNameIgnoreCaseAndLevel(l1Import.getName(), l0Import.getName(), 1);
        } else if (l0Import != null) {
            potentials = capabilityRepository.findByNameIgnoreCaseAndParentNameIgnoreCaseAndLevel(l0Import.getName(), null, 0);
        } else {
            throw new IllegalStateException(
                "At least one capability should not be null : " + l0Import + " " + l1Import + " " + l2Import + " " + l3Import
            );
        }
        if (potentials.size() != 1) {
            throw new IllegalStateException(
                "Capability could not be found : " + l0Import + " " + l1Import + " " + l2Import + " " + l3Import
            );
        }
        Capability capability = potentials.get(0);
        return capability;
    }
}
