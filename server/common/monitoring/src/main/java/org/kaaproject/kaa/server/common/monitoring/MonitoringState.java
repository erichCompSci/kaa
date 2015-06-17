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

package org.kaaproject.kaa.server.common.monitoring;

import java.util.concurrent.atomic.AtomicInteger;

public class MonitoringState {

    private AtomicInteger inputTasks = new AtomicInteger();
    private AtomicInteger successTasks = new AtomicInteger();
    private AtomicInteger failureTasks = new AtomicInteger();
    private AtomicInteger pendingTasks = new AtomicInteger();

    public MonitoringState() {
    }

    public void appendInputTaskCount(int count) {
        inputTasks.getAndAdd(count);
        pendingTasks.getAndAdd(count);
    }

    public void appendSuccessTaskCount(int count) {
        successTasks.getAndAdd(count);
        pendingTasks.set(pendingTasks.get() - successTasks.get());
    }

    public void appendFailureTaskCount(int count) {
        failureTasks.getAndAdd(count);
        pendingTasks.set(pendingTasks.get() - failureTasks.get());
    }

    public int getInputTasksCount() {
        return inputTasks.getAndSet(0);
    }

    public int getSuccessTasksCount() {
        return successTasks.getAndSet(0);
    }

    public int getFailureTasksCount() {
        return failureTasks.getAndSet(0);
    }

    public int getPendingTasksCount() {
        return pendingTasks.get();
    }

}
