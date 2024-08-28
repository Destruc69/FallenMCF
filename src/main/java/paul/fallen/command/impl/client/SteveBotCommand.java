package paul.fallen.command.impl.client;

import paul.fallen.FALLENClient;
import paul.fallen.command.Command;
import paul.fallen.utils.client.ClientUtils;
import stevebot.core.StevebotApi;
import stevebot.core.data.blockpos.BaseBlockPos;
import stevebot.core.pathfinding.path.PathRenderable;
import stevebot.core.player.PlayerUtils;

public class SteveBotCommand extends Command {

    private final StevebotApi sbai = FALLENClient.INSTANCE.getStevebotApi();

    public SteveBotCommand() {
        this.setNames(new String[]{"sb"});
    }

    @Override
    public void runCommand(String[] args) {
        String command = args[1];

        switch (command.toLowerCase()) {
            case "pathfromto":
                if (args.length == 8) {
                    BaseBlockPos from = new BaseBlockPos(
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3]),
                            Integer.parseInt(args[4])
                    );
                    BaseBlockPos to = new BaseBlockPos(
                            Integer.parseInt(args[5]),
                            Integer.parseInt(args[6]),
                            Integer.parseInt(args[7])
                    );
                    sbai.path(from, to, false, false);
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathfromto <xs> <ys> <zs> <xd> <yd> <zd>");
                }
                break;

            case "pathto":
                if (args.length == 5) {
                    BaseBlockPos from = PlayerUtils.getPlayerBlockPos();
                    BaseBlockPos to = new BaseBlockPos(
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3]),
                            Integer.parseInt(args[4])
                    );
                    sbai.path(from, to, true, false);
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathto <xs> <ys> <zs>");
                }
                break;

            case "pathtofreelook":
                if (args.length == 5) {
                    BaseBlockPos from = PlayerUtils.getPlayerBlockPos();
                    BaseBlockPos to = new BaseBlockPos(
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3]),
                            Integer.parseInt(args[4])
                    );
                    sbai.path(from, to, true, true);
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathtofreelook <xs> <ys> <zs>");
                }
                break;

            case "pathdir":
                if (args.length == 3) {
                    int dist = Integer.parseInt(args[2]);
                    sbai.pathDirection(dist, true, false);
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathdir <dist>");
                }
                break;

            case "pathdirfreelook":
                if (args.length == 3) {
                    int dist = Integer.parseInt(args[2]);
                    sbai.pathDirection(dist, true, true);
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathdirfreelook <dist>");
                }
                break;

            case "pathlevel":
                if (args.length == 3) {
                    int level = Integer.parseInt(args[2]);
                    sbai.pathLevel(level, true, false);
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathlevel <level>");
                }
                break;

            case "pathlevelfreelook":
                if (args.length == 3) {
                    int level = Integer.parseInt(args[2]);
                    sbai.pathLevel(level, true, true);
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathlevelfreelook <level>");
                }
                break;

            case "pathblock":
                if (args.length == 3) {
                    String block = args[2];
                    sbai.pathBlock(block, true, false);
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathblock <block>");
                }
                break;

            case "pathblockfreelook":
                if (args.length == 3) {
                    String block = args[2];
                    sbai.pathBlock(block, true, true);
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathblockfreelook <block>");
                }
                break;

            case "freelook":
                sbai.toggleFreelook();
                break;

            case "followstop":
                sbai.stopFollowing();
                break;

            case "settimeout":
                if (args.length == 3) {
                    float timeout = Float.parseFloat(args[2]);
                    sbai.setTimeout(timeout);
                } else {
                    ClientUtils.addChatMessage("Usage: sb settimeout <seconds>");
                }
                break;

            case "setverbose":
                if (args.length == 3) {
                    boolean enable = Boolean.parseBoolean(args[2]);
                    sbai.setVerbose(enable);
                } else {
                    ClientUtils.addChatMessage("Usage: sb setverbose <enable>");
                }
                break;

            case "setkeeppathrenderable":
                if (args.length == 3) {
                    boolean keep = Boolean.parseBoolean(args[2]);
                    sbai.setKeepPath(keep);
                } else {
                    ClientUtils.addChatMessage("Usage: sb setkeeppathrenderable <keep>");
                }
                break;

            case "setslowdown":
                if (args.length == 3) {
                    int slowdown = Integer.parseInt(args[2]);
                    sbai.setPathfindingSlowdown(slowdown);
                } else {
                    ClientUtils.addChatMessage("Usage: sb setslowdown <milliseconds>");
                }
                break;

            case "showchunkcache":
                if (args.length == 3) {
                    boolean show = Boolean.parseBoolean(args[2]);
                    sbai.setShowChunkCache(show);
                } else {
                    ClientUtils.addChatMessage("Usage: sb showchunkcache <show>");
                }
                break;

            case "shownodecache":
                if (args.length == 3) {
                    boolean show = Boolean.parseBoolean(args[2]);
                    sbai.setShowNodeCache(show);
                } else {
                    ClientUtils.addChatMessage("Usage: sb shownodecache <show>");
                }
                break;

            case "pathstyle":
                if (args.length == 3) {
                    String style = args[2].toLowerCase();
                    switch (style) {
                        case "solid":
                            sbai.setPathDisplayStyle(PathRenderable.PathStyle.SOLID);
                            break;
                        case "pathid":
                            sbai.setPathDisplayStyle(PathRenderable.PathStyle.PATH_ID);
                            break;
                        case "actionid":
                            sbai.setPathDisplayStyle(PathRenderable.PathStyle.ACTION_ID);
                            break;
                        case "actioncost":
                            sbai.setPathDisplayStyle(PathRenderable.PathStyle.ACTION_COST);
                            break;
                        case "actiontype":
                            sbai.setPathDisplayStyle(PathRenderable.PathStyle.ACTION_TYPE);
                            break;
                        default:
                            ClientUtils.addChatMessage("Unknown style: " + style);
                            break;
                    }
                } else {
                    ClientUtils.addChatMessage("Usage: sb pathstyle <style>");
                }
                break;

            case "statistics":
                sbai.printStatistics(false);
                break;

            case "statisticsconsole":
                sbai.printStatistics(true);
                break;

            case "clearblockcache":
                sbai.clearNodeCache();
                break;

            case "actioncoststats":
                sbai.displayActionCostStats();
                break;

            case "actioncostclear":
                sbai.clearActionCostStats();
                break;

            default:
                ClientUtils.addChatMessage("Unknown command: " + command);
                break;
        }
    }

    public String getHelp() {
        return "<sb> - SteveBotAPI";
    }
}
