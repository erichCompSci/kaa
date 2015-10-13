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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kaaproject.avro.ui.gwt.client.widget.grid.event.RowActionEvent;
import org.kaaproject.avro.ui.gwt.client.widget.grid.event.RowActionEventHandler;
import org.kaaproject.kaa.common.dto.EndpointGroupDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.EndpointProfilesPageDto;
import org.kaaproject.kaa.server.admin.client.KaaAdmin;
import org.kaaproject.kaa.server.admin.client.mvp.ClientFactory;
import org.kaaproject.kaa.server.admin.client.mvp.place.EndpointProfilePlace;
import org.kaaproject.kaa.server.admin.client.mvp.place.EndpointProfilesPlace;
import org.kaaproject.kaa.server.admin.client.mvp.view.BaseListView;
import org.kaaproject.kaa.server.admin.client.mvp.view.EndpointProfilesView;
import org.kaaproject.kaa.server.admin.client.util.Utils;
import org.kaaproject.kaa.server.admin.shared.services.KaaAdminServiceException;
import org.kaaproject.kaa.server.admin.shared.services.ServiceErrorCode;

import java.util.ArrayList;
import java.util.List;

public class EndpointProfilesActivity extends AbstractActivity implements BaseListView.Presenter {

    private String applicationId;
    public static final String DEFAULT_LIMIT = "10";   // ten per page is optimal
    public static final String DEFAULT_OFFSET = "0";

    protected final ClientFactory clientFactory;

    private List<HandlerRegistration> registrations = new ArrayList<>();

    private EndpointProfilesView listView;
    private EndpointProfilesPlace place;
//    private boolean pagerRemoved;
//
//    private SimplePager pager;

    public EndpointProfilesActivity(EndpointProfilesPlace place, ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
        this.applicationId = place.getApplicationId();
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        listView = clientFactory.getEndpointProfilesView();
        getGroupsList();
          /*
              Replace default pager with custom
           */
        listView.setPresenter(this);
        bind();
        containerWidget.setWidget(listView.asWidget());
    }

    private void loadDataDirectly(String groupID, String limit, String offset) {
        KaaAdmin.getDataSource().getEndpointProfileByGroupID(groupID, limit, offset,
                new AsyncCallback<EndpointProfilesPageDto>() {
                    @Override
                    public void onFailure(Throwable caught) {
                            /*
                                dirty hack because of exception throwing on empty list in
                                KaaAdminServiceImpl#getEndpointProfileByEndpointGroupId()
                             */
                        if (caught instanceof KaaAdminServiceException) {
                            if (((KaaAdminServiceException) caught).getErrorCode() == ServiceErrorCode.ITEM_NOT_FOUND) {
                                listView.getListWidget().getDataGrid().setRowData(new ArrayList<EndpointProfileDto>());
                            }
                        } else Utils.handleException(caught, listView);
                    }

                    @Override
                    public void onSuccess(EndpointProfilesPageDto result) {
                        listView.getListWidget().getDataGrid().setRowData(result.getEndpointProfiles());
                    }
                });
    }

    private void getGroupsList() {
        KaaAdmin.getDataSource().loadEndpointGroups(applicationId, new AsyncCallback<List<EndpointGroupDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                Utils.handleException(caught, listView);
            }
            @Override
            public void onSuccess(List<EndpointGroupDto> result) {
                populateListBox(result);
            }
        });
    }

    private void populateListBox(List<EndpointGroupDto> result) {
        for (EndpointGroupDto endGroup: result) {
            if (endGroup.getWeight() == 0) {
                loadDataDirectly(endGroup.getId(), DEFAULT_LIMIT, DEFAULT_OFFSET);
                listView.getEndpointGroupsInfo().setValue(endGroup);
            }
        }
        listView.getEndpointGroupsInfo().setAcceptableValues(result);
    }

    private void bind() {

        listView.clearError();

        registrations.add(listView.getRowActionsSource().addRowActionHandler(new RowActionEventHandler<String>() {
            @Override
            public void onRowAction(RowActionEvent<String> event) {
                String id = event.getClickedId();
                if (event.getAction()==RowActionEvent.CLICK) {
                    goTo(new EndpointProfilePlace(applicationId, id));
                }
            }
        }));

        registrations.add(listView.getEndpointGroupsInfo().addValueChangeHandler(new ValueChangeHandler<EndpointGroupDto>() {
            @Override
            public void onValueChange(ValueChangeEvent<EndpointGroupDto> valueChangeEvent) {
                loadDataDirectly(valueChangeEvent.getValue().getId(), DEFAULT_LIMIT, DEFAULT_OFFSET);
            }
        }));

        registrations.add(listView.getFindEndpointButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                findEndpointFromThisApplication(listView.getEndpointKeyHashTextBox().getValue());
            }
        }));

        final Place previousPlace = place.getPreviousPlace();
        if (previousPlace != null) {
            listView.setBackEnabled(true);
            registrations.add(listView.getBackButton().addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    goTo(previousPlace);
                }
            }));
        }
    }

    private void findEndpointFromThisApplication(String endpointKeyHash) {
        KaaAdmin.getDataSource().getEndpointProfileByKeyHash(endpointKeyHash, new AsyncCallback<EndpointProfileDto>() {
            @Override
            public void onFailure(Throwable caught) {
                Utils.handleException(caught, listView);
            }

            @Override
            public void onSuccess(EndpointProfileDto endpointProfileDto) {
                List<EndpointProfileDto> result = new ArrayList<>();
                if (endpointProfileDto.getApplicationId().equals(applicationId)) {
                    result.add(endpointProfileDto);
                }
                listView.getListWidget().getDataGrid().setRowData(result);
            }
        });
    }

    @Override
    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }

    @Override
    public void onStop() {
        for (HandlerRegistration registration : registrations) {
            registration.removeHandler();
        }
        registrations.clear();
    }
}
