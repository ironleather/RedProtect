/*
 * Copyright (c) 2019 - @FabioZumbi12
 * Last Modified: 25/04/19 07:02
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package br.net.fabiozumbi12.RedProtect.Sponge.config;

import br.net.fabiozumbi12.RedProtect.Core.config.GuiLangCore;
import br.net.fabiozumbi12.RedProtect.Core.helpers.CoreUtil;
import br.net.fabiozumbi12.RedProtect.Sponge.RedProtect;
import br.net.fabiozumbi12.RedProtect.Sponge.helpers.RedProtectUtil;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LangGuiManager extends GuiLangCore {

    public LangGuiManager() {
        String resLang = "gui" + RedProtect.get().config.configRoot().language + ".properties";
        pathLang = RedProtect.get().configDir + File.separator + resLang;

        File lang = new File(pathLang);
        if (!lang.exists()) {
            try {
                if (RedProtect.get().container.getAsset(resLang).isPresent()) {
                    RedProtect.get().container.getAsset(resLang).get().copyToDirectory(RedProtect.get().configDir.toPath());
                } else {
                    RedProtect.get().container.getAsset("guiEN-US.properties").get().copyToDirectory(RedProtect.get().configDir.toPath());
                    new File(RedProtect.get().configDir,"guiEN-US.properties").renameTo(lang);
                }
            } catch (IOException e) {
                CoreUtil.printJarVersion();
                e.printStackTrace();
            }
            RedProtect.get().logger.info("Created GUI language file: " + pathLang);
        }

        loadLang();
        loadBaseLang();

        // Restore form backup
        if (!RedProtect.get().config.backupGuiName.isEmpty()){
            RedProtect.get().config.backupGuiName.forEach((k,v) -> loadedLang.put("gui.flags." + k + ".name", v));
        }
        if (!RedProtect.get().config.backupGuiDescription.isEmpty()){
            RedProtect.get().config.backupGuiDescription.forEach((k,v) -> loadedLang.put("gui.flags." + k + ".description", v));
        }

        updateLang();
    }

    private void loadLang() {
        loadDefaultLang();

        if (loadedLang.get("_lang.version") != null) {
            int langv = Integer.parseInt(loadedLang.get("_lang.version").toString().replace(".", ""));
            int rpv = Integer.parseInt(RedProtect.get().container.getVersion().get().replace(".", ""));
            if (langv < rpv || langv == 0) {
                loadedLang.put("_lang.version", RedProtect.get().container.getVersion().get());
            }
        }
    }

    private void updateLang() {
        updateLang(RedProtect.get().container.getVersion().get());
    }

    public String getFlagName(String flag) {
        String flagName = getRaw("gui.flags." + flag + ".name");
        if (flagName == null){
            flagName = getRaw("gui.flags.default.name");
        }
        return flagName;
    }

    public List<Text> getFlagDescription(String flag) {
        String flagDescription = getRaw("gui.flags." + flag + ".description");
        if (flagDescription == null){
            flagDescription = getRaw("gui.flags.default.description");
        }
        return Arrays.stream(flagDescription.split("/n")).map(RedProtectUtil::toText).collect(Collectors.toList());
    }

    public Text getFlagString(String key) {
        return RedProtectUtil.toText(getRaw("gui.strings." + key));
    }
}
