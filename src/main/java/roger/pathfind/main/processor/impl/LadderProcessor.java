package roger.pathfind.main.processor.impl;

import roger.pathfind.main.path.PathElm;
import roger.pathfind.main.path.impl.LadderNode;
import roger.pathfind.main.path.impl.TravelVector;
import roger.pathfind.main.processor.Processor;

import java.util.ArrayList;
import java.util.List;

public class LadderProcessor extends Processor {

    @Override
    public void process(List<PathElm> elms) {
        List<PathElm> newPath = new ArrayList<>();

        // Iterate over the elements to identify and connect ladder nodes
        PathIter:
        for (int a = 0; a < elms.size(); a++) {
            PathElm elm = elms.get(a);

            if (!(elm instanceof LadderNode)) {
                newPath.add(elm);
                continue;
            }

            LadderNode start = (LadderNode) elm;

            for (int b = elms.size() - 1; b > a; b--) {
                if (!(elms.get(b) instanceof LadderNode)) {
                    continue;
                }

                LadderNode end = (LadderNode) elms.get(b);

                // Connect the ladder nodes if they are vertically aligned and properly ordered
                if (shouldConnect(start, end)) {
                    // Connect the bottom of the ladder start to the top of the ladder end
                    newPath.add(new TravelVector(start, end));
                    a = b;
                    continue PathIter;
                }
            }

            newPath.add(elm);
        }

        elms.clear();
        elms.addAll(newPath);
    }

    private boolean shouldConnect(LadderNode start, LadderNode end) {
        // Check if the ladders are vertically aligned
        return start.getX() == end.getX() && start.getZ() == end.getZ()
                && start.getY() < end.getY();
    }
}