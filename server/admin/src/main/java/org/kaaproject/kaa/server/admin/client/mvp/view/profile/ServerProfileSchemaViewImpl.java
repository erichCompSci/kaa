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

package org.kaaproject.kaa.server.admin.client.mvp.view.profile;

import org.kaaproject.kaa.server.admin.client.mvp.view.schema.BaseSchemaViewImpl;
import org.kaaproject.kaa.server.admin.client.util.Utils;

public class ServerProfileSchemaViewImpl extends BaseSchemaViewImpl {

    public ServerProfileSchemaViewImpl(boolean create) {
        super(create);
    }

    @Override
    protected String getCreateTitle() {
        return "Edit server profile schema";
    }

    @Override
    protected String getViewTitle() {
        return Utils.constants.profileSchema();
    }

    @Override
    protected String getSubTitle() {
        return "Server profiles schema details";
    }

    protected void updateSaveButton(boolean enabled, boolean invalid) {
        if (create || invalid) {
            saveButton.setText(create ? Utils.constants.add() : Utils.constants.save());
        } else {
            saveButton.setText(enabled ? Utils.constants.save() : Utils.constants.saved());
        }
        saveButton.setEnabled(enabled);
    }
}
