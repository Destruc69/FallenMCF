/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import paul.fallen.FALLENClient;
import paul.fallen.module.Module;

public final class HideMyAss extends Module {

    public HideMyAss(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }


    @Override
    public void onEnable() {
        try {
            for (Module hack : FALLENClient.INSTANCE.getModuleManager().getModules()) {
                if (hack.getState()) {
                    hack.setState(false);
                }
            }
            setState(false);
        } catch (Exception ignored) {
        }
    }
}