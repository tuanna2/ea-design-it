<template>
  <div class="row justify-content-center">
    <div class="col-12">
      <div v-if="functionalFlow">
        <h2 class="jh-entity-heading" data-cy="functionalFlowDetailsHeading">
          <font-awesome-icon icon="project-diagram" style="color: Tomato; font-size: 0.7em"></font-awesome-icon>
          <span>Functional Flow</span> - {{ functionalFlow.alias }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Alias</span>
          </dt>
          <dd>
            <span>{{ functionalFlow.alias }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ functionalFlow.description }}</span>
          </dd>
          <dt>
            <span>Comment</span>
          </dt>
          <dd>
            <span>{{ functionalFlow.comment }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ functionalFlow.status }}</span>
          </dd>
          <dt>
            <span>Documentation URL</span>
          </dt>
          <dd>
            <span
              ><a v-bind:href="functionalFlow.documentationURL">{{
                functionalFlow.documentationURL ? functionalFlow.documentationURL.substring(0, 200) : ''
              }}</a></span
            >
          </dd>
          <dt>
            <span>Documentation URL 2</span>
          </dt>
          <dd>
            <span
              ><a v-bind:href="functionalFlow.documentationURL2">{{
                functionalFlow.documentationURL2 ? functionalFlow.documentationURL2.substring(0, 200) : ''
              }}</a></span
            >
          </dd>
          <dt>
            <span>Start Date</span>
          </dt>
          <dd>
            <span>{{ functionalFlow.startDate }}</span>
          </dd>
          <dt>
            <span>End Date</span>
          </dt>
          <dd>
            <span>{{ functionalFlow.endDate }}</span>
          </dd>
          <dt>
            <span>Owner</span>
          </dt>
          <dd>
            <div v-if="functionalFlow.owner">
              <router-link :to="{ name: 'OwnerView', params: { ownerId: functionalFlow.owner.id } }">{{
                functionalFlow.owner.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
        </button>
        <router-link
          v-if="functionalFlow.id"
          :to="{ name: 'FunctionalFlowEdit', params: { functionalFlowId: functionalFlow.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary" v-if="accountService().writeAuthorities">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span> Edit Information</span>
          </button>
        </router-link>
      </div>

      <br />

      <h3>{{ functionalFlow.alias }} - {{ functionalFlow.description }}</h3>

      <div>
        <div v-html="plantUMLImage"></div>
        <div class="col-12">
          <!-- <button class="btn btn-success float-right" v-on:click="exportDiagram()" style="font-size: 0.7em; padding: 3px; margin: 3px">
            <font-awesome-icon icon="sync"></font-awesome-icon> <span>Export</span>
          </button>         -->
          <button
            class="btn btn-warning"
            v-on:click="exportPlantUML()"
            style="font-size: 0.7em; padding: 3px; margin: 3px"
            v-if="plantUMLImage"
            :disabled="isFetching"
          >
            <span>Export plantuml</span>
          </button>
          <button
            class="btn btn-secondary"
            v-on:click="changeDiagram()"
            style="font-size: 0.7em; padding: 3px; margin: 3px"
            :disabled="isFetching"
          >
            <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
            <span v-text="sequenceDiagram ? 'component diagram' : 'sequence diagram'" />
          </button>
        </div>
      </div>
      <div class="table-responsive" v-if="functionalFlow.steps && functionalFlow.steps.length > 0">
        <table class="table table-striped">
          <thead>
            <tr>
              <th scope="row"><span>Step</span></th>
              <th scope="row" v-if="reorderAlias"></th>
              <th scope="row" v-if="reorderAlias"></th>
              <th scope="row"><span>Interface</span></th>
              <th scope="row"><span>Source</span></th>
              <th scope="row"><span>Target</span></th>
              <th scope="row"><span>Protocol</span></th>
              <th scope="row"><span>Data Flows</span></th>
              <th scope="row"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(step, i) in functionalFlow.steps" v-bind:key="step.id" :set="(inter = step.flowInterface)">
              <td>
                {{ step.stepOrder }}.
                <span v-if="!reorderAlias">{{ step.description }}</span>
                <span v-else>
                  <textarea
                    style="width: 100%; min-width: 600px"
                    rows="1"
                    v-model="step.description"
                    @change="changeStepDescription(functionalFlow, step)"
                  ></textarea>
                </span>
              </td>
              <td v-if="reorderAlias">
                <font-awesome-icon icon="chevron-up" class="btn-success" v-if="i != 0" @click="swap(i, i - 1)"></font-awesome-icon>
              </td>
              <td v-if="reorderAlias">
                <font-awesome-icon
                  icon="chevron-down"
                  class="btn-success"
                  v-if="i != functionalFlow.steps.length - 1"
                  @click="swap(i, i + 1)"
                ></font-awesome-icon>
              </td>
              <td>
                <router-link :to="{ name: 'FlowInterfaceView', params: { flowInterfaceId: inter.id } }">{{ inter.alias }}</router-link>
              </td>
              <td>
                <router-link :to="{ name: 'ApplicationView', params: { applicationId: inter.source.id } }">
                  {{ inter.source.name }}
                </router-link>
                <span v-if="step.flowInterface.id && step.flowInterface.sourceComponent">
                  /
                  <router-link
                    :to="{ name: 'ApplicationComponentView', params: { applicationComponentId: step.flowInterface.sourceComponent.id } }"
                    >{{ step.flowInterface.sourceComponent.name }}</router-link
                  >
                </span>
              </td>
              <td>
                <router-link :to="{ name: 'ApplicationView', params: { applicationId: inter.target.id } }">
                  {{ inter.target.name }}
                </router-link>
                <span v-if="step.flowInterface.id && step.flowInterface.targetComponent">
                  /
                  <router-link
                    :to="{ name: 'ApplicationComponentView', params: { applicationComponentId: step.flowInterface.targetComponent.id } }"
                    >{{ step.flowInterface.targetComponent.name }}</router-link
                  >
                </span>
              </td>
              <td>
                <router-link v-if="inter.protocol" :to="{ name: 'ProtocolView', params: { protocolId: inter.protocol.id } }">
                  {{ inter.protocol.name }}
                </router-link>
              </td>
              <td>
                <span v-for="dataflow in inter.dataFlows" :key="dataflow.id">
                  <router-link
                    :to="{ name: 'DataFlowView', params: { dataFlowId: dataflow.id } }"
                    :title="dataflow.resourceName + (dataflow.items.length > 0 ? ' / ' + dataflow.items.length + ' items ' : ' / no items')"
                    >{{ dataflow.id }}</router-link
                  ><sup v-if="dataflow.items && dataflow.items.length > 0">({{ dataflow.items.length }})</sup>&nbsp;
                </span>
              </td>
              <td class="text-right">
                <div class="btn-group">
                  <router-link :to="{ name: 'FlowInterfaceView', params: { flowInterfaceId: inter.id } }" custom v-slot="{ navigate }">
                    <button
                      @click="navigate"
                      class="btn btn-info btn-sm details"
                      data-cy="entityDetailsButton"
                      v-if="!accountService().writeAuthorities"
                    >
                      <font-awesome-icon icon="eye"></font-awesome-icon>
                      <span class="d-none d-md-inline">View</span>
                    </button>
                    <button
                      @click="navigate"
                      class="btn btn-primary btn-sm edit"
                      data-cy="entityEditButton"
                      v-if="accountService().writeAuthorities"
                    >
                      <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                      <span class="d-none d-md-inline">Edit</span>
                    </button>
                  </router-link>
                  <b-button v-if="accountService().writeAuthorities" v-on:click="prepareToDetach(i)" variant="warning" class="btn btn-sm">
                    <font-awesome-icon icon="times"></font-awesome-icon>
                    <span class="d-none d-md-inline">Detach</span>
                  </b-button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="row">
        <div class="col-md-6">
          <button
            @click="startReorder()"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-success jh-create-entity create-functional-flow"
            title="Edit Flow Alias in order to move interfaces from on flow to another"
            v-if="accountService().writeAuthorities && !reorderAlias"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span> Organize Flows</span>
          </button>

          <button
            @click="saveReorder()"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-success jh-create-entity create-functional-flow"
            title="Edit Flow Alias in order to move interfaces from on flow to another"
            v-if="reorderAlias && (reorderAliasflowToSave.length > 0 || reorderStepToSave.length > 0)"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Save</span>
          </button>

          <button
            @click="cancelReorder()"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-success jh-create-entity create-functional-flow"
            title="Edit Flow Alias in order to move interfaces from on flow to another"
            v-if="reorderAlias"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Cancel</span>
          </button>
        </div>

        <div class="d-flex justify-content-end col-md-6">
          <span>
            <button
              class="btn btn-primary jh-create-entity create-functional-flow"
              v-if="accountService().writeAuthorities"
              @click="prepareSearchInterfaces()"
            >
              <font-awesome-icon icon="plus"></font-awesome-icon>
              <span>Add Interface</span>
            </button>
            <button
              class="btn btn-primary jh-create-entity create-functional-flow"
              v-if="accountService().writeAuthorities && toBeSaved"
              @click="saveFunctionalFlow()"
            >
              <font-awesome-icon icon="plus"></font-awesome-icon>
              <span>Save</span>
            </button>
          </span>
        </div>
      </div>

      <div class="table-responsive" v-if="functionalFlow.landscapes && functionalFlow.landscapes.length > 0">
        <br />
        <br />
        <h3>
          <font-awesome-icon icon="map" style="color: Tomato; font-size: 0.7em"></font-awesome-icon> Landscapes using Functional Flow
          {{ functionalFlow.alias }}
        </h3>

        <table class="table table-striped">
          <thead>
            <tr>
              <th scope="row"><span>Id</span></th>
              <th scope="row"><span>Diagram Name</span></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="landscape in functionalFlow.landscapes" v-bind:key="landscape.id">
              <td>
                <router-link :to="{ name: 'LandscapeViewView', params: { landscapeViewId: landscape.id } }">{{ landscape.id }}</router-link>
              </td>
              <td>
                <router-link :to="{ name: 'LandscapeViewView', params: { landscapeViewId: landscape.id } }">{{
                  landscape.diagramName
                }}</router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <b-modal ref="searchEntity" id="searchEntity" class="mymodalclass">
      <span slot="modal-title"
        ><span id="eaDesignItApp.flowInterface.delete.question" data-cy="flowInterfaceDeleteDialogHeading"
          >Find or create Interface</span
        ></span
      >
      <div class="modal-body">
        <datalist id="applications">
          <option v-for="applcation in applications" :key="applcation.id">{{ applcation.name }}</option>
        </datalist>
        <datalist id="protocols">
          <option v-for="protocol in protocols" :key="protocol.id">{{ protocol.name }}</option>
        </datalist>
        <span>
          Source : <input list="applications" v-model="searchSourceName" /> Target :
          <input list="applications" v-model="searchTargetName" /> Protocol : <input list="protocols" v-model="searchProtocolName" />
          <button type="button" class="btn btn-primary" v-on:click="searchInterfaces()">Search Interface</button>
        </span>
        <div class="table-responsive" v-if="interfaces && interfaces.length > 0">
          <table class="table">
            <thead>
              <tr>
                <th></th>
                <th scope="row"><span>Alias</span></th>
                <th scope="row"><span>Source</span></th>
                <th scope="row"><span>Target</span></th>
                <th scope="row"><span>Protocol</span></th>
                <th scope="row"><span>DataFlows</span></th>
                <th scope="row"><span>Functional Flows (used by)</span></th>
              </tr>
            </thead>
            <tr v-for="inter in interfaces" :key="inter.id">
              <td>
                <input type="checkbox" :id="inter.id" :value="inter" v-model="checkedInterface" />
              </td>
              <td>{{ inter.alias }}</td>
              <td>{{ inter.source.name }}</td>
              <td>{{ inter.target.name }}</td>
              <td>
                <span v-if="inter.protocol">{{ inter.protocol.name }}</span>
              </td>
              <td>
                <span
                  v-for="dataflow in inter.dataFlows"
                  :key="dataflow.id"
                  :title="'frequency : [' + dataflow.frequency + ']' + (dataflow.format ? ', format : [' + dataflow.format.name + ']' : '')"
                >
                  {{ dataflow.resourceName }}
                </span>
              </td>
              <td>
                <span v-for="flow in inter.functionalFlows" :key="flow.id" :title="flow.description">
                  {{ flow.alias }}
                </span>
              </td>
            </tr>
          </table>
        </div>
        <div v-if="searchDone && (!checkedInterface || checkedInterface.length === 0)">
          <p />
          <p>Nothing found. Do you want to create new Interface?</p>
        </div>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-on:click="closeSearchDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="mergeflowInterfaceButtonID"
          ref="mergeflowInterfaceButtonRef"
          data-cy="entityConfirmDeleteButton"
          v-on:click="addOrCreateInterface()"
          v-if="checkedInterface && checkedInterface.length > 0"
        >
          Add
        </button>
        <router-link
          :to="{
            name: 'FlowInterfaceCreate',
            query: {
              functionalFlowId: functionalFlow.id,
              sourceId: searchSourceId,
              targetId: searchTargetId,
              protocolId: searchProtocolId,
            },
          }"
          custom
          v-slot="{ navigate }"
          v-if="accountService().writeAuthorities"
        >
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-flow-interface"
            v-if="searchDone && (!checkedInterface || checkedInterface.length === 0)"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span> Create a new Flow Interface </span>
          </button>
        </router-link>
      </div>
    </b-modal>
    <b-modal ref="detachInterfaceEntity" id="detachInterfaceEntity">
      <span slot="modal-title"
        ><span id="eaDesignItApp.landscapeView.delete.question" data-cy="landscapeViewDeleteDialogHeading"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-landscapeView-heading">Are you sure you want to detach this Interface?</p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-on:click="closeDetachDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-landscapeView"
          data-cy="entityConfirmDeleteButton"
          v-on:click="detachInterface()"
        >
          Detach
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./functional-flow-details.component.ts"></script>

<style>
.modal-dialog {
  max-width: 80%;
}
.fa-project-diagram g g path {
  stroke: red;
  stroke-width: 10;
}
</style>
