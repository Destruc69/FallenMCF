package paul.fallen.command;

import paul.fallen.ClientSupport;

public class Command implements ClientSupport {

    private String[] names;

    public void runCommand(String[] args) {
    }

    public String getHelp() {
        return null;
    }

    public String[] getNames() {
        return this.names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

}
