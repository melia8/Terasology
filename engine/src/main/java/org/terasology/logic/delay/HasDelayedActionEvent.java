/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.logic.delay;

import org.terasology.entitySystem.event.Event;

/**
 * @deprecated Use DelayManager::hasDelayedAction instead.
 */
@Deprecated
public class HasDelayedActionEvent implements Event {
    private String actionId;
    private boolean result;

    public HasDelayedActionEvent(String actionId) {
        this.actionId = actionId;
    }

    public String getActionId() {
        return actionId;
    }

    public boolean hasAction() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
