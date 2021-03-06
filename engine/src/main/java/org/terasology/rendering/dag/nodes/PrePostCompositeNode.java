/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.rendering.dag.nodes;

import org.terasology.assets.ResourceUrn;
import org.terasology.monitoring.PerformanceMonitor;
import org.terasology.registry.In;
import org.terasology.rendering.dag.AbstractNode;
import org.terasology.rendering.dag.stateChanges.BindFBO;
import org.terasology.rendering.dag.stateChanges.EnableMaterial;
import org.terasology.rendering.dag.stateChanges.SetViewportToSizeOf;
import static org.terasology.rendering.opengl.DefaultDynamicFBOs.READ_ONLY_GBUFFER;
import static org.terasology.rendering.opengl.DefaultDynamicFBOs.WRITE_ONLY_GBUFFER;
import org.terasology.rendering.opengl.FBO;
import org.terasology.rendering.opengl.FBOConfig;
import static org.terasology.rendering.opengl.ScalingFactors.FULL_SCALE;
import org.terasology.rendering.opengl.fbms.DisplayResolutionDependentFBOs;
import org.terasology.rendering.world.WorldRenderer;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.terasology.rendering.opengl.OpenGLUtils.renderFullscreenQuad;

/**
 * TODO: Add diagram of this node
 */
public class PrePostCompositeNode extends AbstractNode {
    public static final ResourceUrn REFLECTIVE_REFRACTIVE = new ResourceUrn("engine:sceneReflectiveRefractive");

    @In
    private DisplayResolutionDependentFBOs displayResolutionDependentFBOs;

    @In
    private WorldRenderer worldRenderer;

    private FBO sceneReflectiveRefractive;

    @Override
    public void initialise() {
        requiresFBO(new FBOConfig(REFLECTIVE_REFRACTIVE, FULL_SCALE, FBO.Type.HDR).useNormalBuffer(), displayResolutionDependentFBOs);
        addDesiredStateChange(new EnableMaterial("engine:prog.combine"));
        addDesiredStateChange(new BindFBO(WRITE_ONLY_GBUFFER));
        // TODO: verify if there should be bound textures after bind.
        addDesiredStateChange(new SetViewportToSizeOf(WRITE_ONLY_GBUFFER));
    }

    /**
     * Adds outlines and ambient occlusion to the rendering obtained so far stored in the primary FBO.
     * Stores the resulting output back into the primary buffer.
     */
    @Override
    public void process() {
        PerformanceMonitor.startActivity("rendering/prePostComposite");
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // TODO: verify this is necessary

        renderFullscreenQuad();
        displayResolutionDependentFBOs.swapReadWriteBuffers();

        sceneReflectiveRefractive = displayResolutionDependentFBOs.get(REFLECTIVE_REFRACTIVE);
        READ_ONLY_GBUFFER.attachDepthBufferTo(sceneReflectiveRefractive);
        // TODO: verify why we can't move the buffer attachment to before the swap by using WRITE_ONLY_GBUFFER instead.
        // TODO: See right-side streaks in https://cloud.githubusercontent.com/assets/136392/17794231/456f542a-65b6-11e6-83bb-f2cc3f10ee66.png
        PerformanceMonitor.endActivity();
    }
}
