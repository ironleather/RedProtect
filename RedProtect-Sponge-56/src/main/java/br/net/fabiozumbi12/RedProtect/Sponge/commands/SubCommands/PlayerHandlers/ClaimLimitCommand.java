/*
 *  Copyright (c) 2019 - @FabioZumbi12
 *  Last Modified: 31/03/19 21:55
 *
 *  This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *   damages arising from the use of this class.
 *
 *  Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 *  redistribute it freely, subject to the following restrictions:
 *  1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 *  use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 *  2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 *  3 - This notice may not be removed or altered from any source distribution.
 *
 *  Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 *  responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 *  É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 *  alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 *  1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *   classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 *  2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 *  classe original.
 *  3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package br.net.fabiozumbi12.RedProtect.Sponge.commands.SubCommands.PlayerHandlers;

import br.net.fabiozumbi12.RedProtect.Sponge.RedProtect;
import br.net.fabiozumbi12.RedProtect.Sponge.config.LangManager;
import br.net.fabiozumbi12.RedProtect.Sponge.helpers.RPUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import static br.net.fabiozumbi12.RedProtect.Sponge.commands.CommandHandlers.HandleHelpPage;

public class ClaimLimitCommand {

    public CommandSpec register() {
        return CommandSpec.builder()
                .description(Text.of("Command to check claim limits."))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("player"))))
                .permission("redprotect.command.claimlimit")
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        HandleHelpPage(src, 1);
                    } else {
                        Player player = (Player) src;

                        if (!args.hasAny("player")) {
                            int limit = RedProtect.get().ph.getPlayerClaimLimit(player);
                            if (limit < 0 || RedProtect.get().ph.hasPerm(player, "redprotect.limits.claim.unlimited")) {
                                RedProtect.get().lang.sendMessage(player, "cmdmanager.nolimit");
                                return CommandResult.success();
                            }

                            int currentUsed = RedProtect.get().rm.getRegions(RPUtil.PlayerToUUID(player.getName()), player.getWorld()).size();
                            RedProtect.get().lang.sendMessage(player, RedProtect.get().lang.get("cmdmanager.yourclaims") + currentUsed + RedProtect.get().lang.get("general.color") + "/&e" + limit + RedProtect.get().lang.get("general.color"));
                            return CommandResult.success();
                        }

                        if (args.hasAny("player") && RedProtect.get().ph.hasPerm(player, "redprotect.command.admin.claimlimit")) {
                            User offp = RPUtil.getUser(args.<String>getOne("player").get());

                            if (offp == null) {
                                RedProtect.get().lang.sendMessage(player, RedProtect.get().lang.get("cmdmanager.noplayer.thisname").replace("{player}", args.<String>getOne("player").get()));
                                return CommandResult.success();
                            }
                            int limit = RedProtect.get().ph.getPlayerClaimLimit(offp);
                            if (limit < 0 || RedProtect.get().ph.hasPerm(offp, "redprotect.limits.claim.unlimited")) {
                                RedProtect.get().lang.sendMessage(player, "cmdmanager.nolimit");
                                return CommandResult.success();
                            }

                            int currentUsed = RedProtect.get().rm.getRegions(RPUtil.PlayerToUUID(offp.getName()), player.getWorld()).size();
                            RedProtect.get().lang.sendMessage(player, RedProtect.get().lang.get("cmdmanager.yourclaims") + currentUsed + RedProtect.get().lang.get("general.color") + "/&e" + limit + RedProtect.get().lang.get("general.color"));
                            return CommandResult.success();
                        }


                        RedProtect.get().lang.sendCommandHelp(src, "claimlimit", true);
                        return CommandResult.success();
                    }
                    return CommandResult.success();
                }).build();
    }
}