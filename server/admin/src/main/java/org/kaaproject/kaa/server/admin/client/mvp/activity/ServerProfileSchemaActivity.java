/*
 * Copyright 2015 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.server.admin.client.mvp.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.kaaproject.avro.ui.gwt.client.util.BusyAsyncCallback;
import org.kaaproject.avro.ui.shared.RecordField;
import org.kaaproject.kaa.common.dto.ProfileSchemaDto;
import org.kaaproject.kaa.server.admin.client.KaaAdmin;
import org.kaaproject.kaa.server.admin.client.mvp.ClientFactory;
import org.kaaproject.kaa.server.admin.client.mvp.place.ServerProfileSchemaPlace;
import org.kaaproject.kaa.server.admin.client.mvp.view.BaseSchemaView;
import org.kaaproject.kaa.server.admin.client.util.Utils;

public class ServerProfileSchemaActivity extends
        AbstractSchemaActivity<ProfileSchemaDto, BaseSchemaView, ServerProfileSchemaPlace> {

    public ServerProfileSchemaActivity(ServerProfileSchemaPlace place, ClientFactory clientFactory) {
        super(place, clientFactory);
    }

    @Override
    protected BaseSchemaView getView(boolean create) {
        return clientFactory.getServerProfileSchemaView();
    }

    @Override
    protected void getEntity(String id, AsyncCallback<ProfileSchemaDto> callback) {
        KaaAdmin.getDataSource().getProfileSchemaForm(id, callback);
    }

    @Override
    protected void onSave() {
        super.onSave();
        GWT.log("\nschema:\n" + detailsView.getSchemaForm().getValue().getSchema());
    }

    @Override
    protected void editEntity(ProfileSchemaDto entity, AsyncCallback<ProfileSchemaDto> callback) {
        GWT.log("\nsaving server profile!");
        KaaAdmin.getDataSource().editProfileSchemaForm(entity, callback);
    }

    @Override
    protected ProfileSchemaDto newSchema() {
        return new ProfileSchemaDto();
    }

    @Override
    protected void createEmptySchemaForm(AsyncCallback<RecordField> callback) {
        KaaAdmin.getDataSource().createSimpleEmptySchemaForm(callback);
    }

    @Override
    public void loadFormData(String fileItemName, AsyncCallback<RecordField> callback) {
        KaaAdmin.getDataSource().generateSimpleSchemaForm(fileItemName, callback);
    }

    @Override
    protected void onEntityRetrieved() {
        String version = entity.getMajorVersion() + "." + entity.getMinorVersion();
        detailsView.getVersion().setValue(version);
        detailsView.getName().setValue(entity.getName());
        detailsView.getDescription().setValue(entity.getDescription());
        detailsView.getCreatedUsername().setValue(entity.getCreatedUsername());
        detailsView.getCreatedDateTime().setValue(Utils.millisecondsToDateTimeString(entity.getCreatedTime()));
        detailsView.getEndpointCount().setValue(entity.getEndpointCount() + "");
        /*
            must bee all the time true
         */
        if (create) {
            createEmptySchemaForm(new BusyAsyncCallback<RecordField>() {
                @Override
                public void onSuccessImpl(RecordField result) {
                    detailsView.getSchemaForm().setValue(result);
                }

                @Override
                public void onFailureImpl(Throwable caught) {
                    Utils.handleException(caught, detailsView);
                }
            });
            detailsView.getSchemaForm().setFormDataLoader(this);
        } else {
            detailsView.getSchemaForm().setValue(entity.getSchemaForm());
            detailsView.getSchemaForm().setFormDataLoader(this);
        }
    }
}
