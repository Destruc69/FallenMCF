/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.world;

import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public final class AntiFog extends Module {

    public AntiFog(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        this.setState(true);
    }

    @SubscribeEvent
    public void onFogRender(EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0.0F);
        event.setCanceled(true);
    }
}