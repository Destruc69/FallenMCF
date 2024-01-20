package paul.fallen.utils.client;

public class Logger {

    public static void log(LogState logState, String s) {
        String prefix = "";
        if (logState == LogState.Normal) {
            prefix = "[FALLEN]: ";
        } else if (logState == LogState.Warning) {
            prefix = "[FALLEN - WARNING]: ";
        } else if (logState == LogState.Error) {
            prefix = "[FALLEN - ERROR]: ";
        }
        System.out.println(prefix + s);
    }

    public enum LogState {
        Normal, Warning, Error
    }

}
