<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="eaDesignItApp.flowInterface.home.createOrEditLabel" data-cy="FlowInterfaceCreateUpdateHeading">
          <font-awesome-icon icon="grip-lines" style="color: Tomato; font-size: 0.9em"></font-awesome-icon> Create or edit a FlowInterface
          <span v-if="this.$route.query.functionalFlowId"> for Function Flow {{ this.$route.query.functionalFlowId }}</span>
        </h2>
        <div>
          <div class="form-group" v-if="flowInterface.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="flowInterface.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="flow-interface-alias">Alias</label>
            <input
              type="text"
              class="form-control"
              name="alias"
              id="flow-interface-alias"
              data-cy="alias"
              :class="{ valid: !$v.flowInterface.alias.$invalid, invalid: $v.flowInterface.alias.$invalid }"
              v-model="$v.flowInterface.alias.$model"
              required
            />
            <div v-if="$v.flowInterface.alias.$anyDirty && $v.flowInterface.alias.$invalid">
              <small class="form-text text-danger" v-if="!$v.flowInterface.alias.required"> This field is required. </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="flow-interface-status">Status</label>
            <input
              type="text"
              class="form-control"
              name="status"
              id="flow-interface-status"
              data-cy="status"
              :class="{ valid: !$v.flowInterface.status.$invalid, invalid: $v.flowInterface.status.$invalid }"
              v-model="$v.flowInterface.status.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="flow-interface-documentationURL">Documentation URL</label>
            <input
              type="text"
              class="form-control"
              name="documentationURL"
              id="flow-interface-documentationURL"
              data-cy="documentationURL"
              :class="{ valid: !$v.flowInterface.documentationURL.$invalid, invalid: $v.flowInterface.documentationURL.$invalid }"
              v-model="$v.flowInterface.documentationURL.$model"
            />
            <div v-if="$v.flowInterface.documentationURL.$anyDirty && $v.flowInterface.documentationURL.$invalid">
              <small class="form-text text-danger" v-if="!$v.flowInterface.documentationURL.maxLength">
                This field cannot be longer than 500 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="flow-interface-documentationURL2">Documentation URL 2</label>
            <input
              type="text"
              class="form-control"
              name="documentationURL2"
              id="flow-interface-documentationURL2"
              data-cy="documentationURL2"
              :class="{ valid: !$v.flowInterface.documentationURL2.$invalid, invalid: $v.flowInterface.documentationURL2.$invalid }"
              v-model="$v.flowInterface.documentationURL2.$model"
            />
            <div v-if="$v.flowInterface.documentationURL2.$anyDirty && $v.flowInterface.documentationURL2.$invalid">
              <small class="form-text text-danger" v-if="!$v.flowInterface.documentationURL2.maxLength">
                This field cannot be longer than 500 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="flow-interface-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="flow-interface-description"
              data-cy="description"
              :class="{ valid: !$v.flowInterface.description.$invalid, invalid: $v.flowInterface.description.$invalid }"
              v-model="$v.flowInterface.description.$model"
            />
            <div v-if="$v.flowInterface.description.$anyDirty && $v.flowInterface.description.$invalid">
              <small class="form-text text-danger" v-if="!$v.flowInterface.description.maxLength">
                This field cannot be longer than 1500 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="flow-interface-startDate">Start Date</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="flow-interface-startDate"
                  v-model="$v.flowInterface.startDate.$model"
                  name="startDate"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="flow-interface-startDate"
                data-cy="startDate"
                type="text"
                class="form-control"
                name="startDate"
                :class="{ valid: !$v.flowInterface.startDate.$invalid, invalid: $v.flowInterface.startDate.$invalid }"
                v-model="$v.flowInterface.startDate.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="flow-interface-endDate">End Date</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="flow-interface-endDate"
                  v-model="$v.flowInterface.endDate.$model"
                  name="endDate"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="flow-interface-endDate"
                data-cy="endDate"
                type="text"
                class="form-control"
                name="endDate"
                :class="{ valid: !$v.flowInterface.endDate.$invalid, invalid: $v.flowInterface.endDate.$invalid }"
                v-model="$v.flowInterface.endDate.$model"
              />
            </b-input-group>
          </div>
          <div class="form-row">
            <div class="form-group col-md-6">
              <label class="form-control-label" for="flow-interface-source">Source</label>
              <select
                :class="{
                  'form-control valid': !$v.flowInterface.source.$invalid,
                  'form-control invalid': $v.flowInterface.source.$invalid,
                }"
                id="flow-interface-source"
                data-cy="source"
                name="source"
                v-model="flowInterface.source"
                required
                @change="flowInterface.sourceComponent = null"
              >
                <option v-if="!flowInterface.source" v-bind:value="null" selected></option>
                <option
                  v-bind:value="
                    flowInterface.source && applicationOption.id === flowInterface.source.id ? flowInterface.source : applicationOption
                  "
                  v-for="applicationOption in applications"
                  :key="applicationOption.id"
                >
                  {{ applicationOption.name }}
                </option>
              </select>
            </div>
            <div class="form-group col-md-6">
              <label class="form-control-label" for="flow-interface-sourceComponent">Source Component</label>
              <select
                class="form-control"
                id="flow-interface-sourceComponent"
                data-cy="sourceComponent"
                name="sourceComponent"
                v-model="flowInterface.sourceComponent"
                @change="changeSource(flowInterface.sourceComponent)"
              >
                <option v-bind:value="null"></option>
                <option
                  v-bind:value="
                    flowInterface.sourceComponent && applicationComponentOption.id === flowInterface.sourceComponent.id
                      ? flowInterface.sourceComponent
                      : applicationComponentOption
                  "
                  v-for="applicationComponentOption in applicationComponents"
                  :key="applicationComponentOption.id"
                >
                  {{ applicationComponentOption.name }}
                </option>
              </select>
            </div>
            <div v-if="$v.flowInterface.source.$anyDirty && $v.flowInterface.source.$invalid">
              <small class="form-text text-danger" v-if="!$v.flowInterface.source.required"> This field is required. </small>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group col-md-6">
              <label class="form-control-label" for="flow-interface-target">Target</label>
              <select
                :class="{
                  'form-control valid': !$v.flowInterface.target.$invalid,
                  'form-control invalid': $v.flowInterface.target.$invalid,
                }"
                id="flow-interface-target"
                data-cy="target"
                name="target"
                v-model="flowInterface.target"
                required
                @change="flowInterface.targetComponent = null"
              >
                <option v-if="!flowInterface.target" v-bind:value="null" selected></option>
                <option
                  v-bind:value="
                    flowInterface.target && applicationOption.id === flowInterface.target.id ? flowInterface.target : applicationOption
                  "
                  v-for="applicationOption in applications"
                  :key="applicationOption.id"
                >
                  {{ applicationOption.name }}
                </option>
              </select>
            </div>
            <div v-if="$v.flowInterface.target.$anyDirty && $v.flowInterface.target.$invalid">
              <small class="form-text text-danger" v-if="!$v.flowInterface.target.required"> This field is required. </small>
            </div>

            <div class="form-group col-md-6">
              <label class="form-control-label" for="flow-interface-targetComponent">Target Component</label>
              <select
                class="form-control"
                id="flow-interface-targetComponent"
                data-cy="targetComponent"
                name="targetComponent"
                v-model="flowInterface.targetComponent"
                @change="changeTarget(flowInterface.targetComponent)"
              >
                <option v-bind:value="null"></option>
                <option
                  v-bind:value="
                    flowInterface.targetComponent && applicationComponentOption.id === flowInterface.targetComponent.id
                      ? flowInterface.targetComponent
                      : applicationComponentOption
                  "
                  v-for="applicationComponentOption in applicationComponents"
                  :key="applicationComponentOption.id"
                >
                  {{ applicationComponentOption.name }}
                </option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="flow-interface-protocol">Protocol</label>
            <select class="form-control" id="flow-interface-protocol" data-cy="protocol" name="protocol" v-model="flowInterface.protocol">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  flowInterface.protocol && protocolOption.id === flowInterface.protocol.id ? flowInterface.protocol : protocolOption
                "
                v-for="protocolOption in protocols"
                :key="protocolOption.id"
              >
                {{ protocolOption.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="flow-interface-owner">Owner</label>
            <select class="form-control" id="flow-interface-owner" data-cy="owner" name="owner" v-model="flowInterface.owner">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="flowInterface.owner && ownerOption.id === flowInterface.owner.id ? flowInterface.owner : ownerOption"
                v-for="ownerOption in owners"
                :key="ownerOption.id"
              >
                {{ ownerOption.name }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.flowInterface.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./flow-interface-update.component.ts"></script>
