import java.util.ArrayList;
import java.util.List;

public class Automata {
    int n;
    int m;
    int s;
    int f;
    Graph graph;
    List<Integer> initialStates;
    List<Integer> finalStates;

    Automata(int n,
             int m,
             int s,
             int f) {
        this.n = n;
        this.m = m;
        this.s = s;
        this.f = f;
        this.graph = new Graph(n);
        this.initialStates = new ArrayList<>();
        this.finalStates = new ArrayList<>();
    }

    void addInitialState(int state) {
        initialStates.add(state);
    }

    void addFinalState(int state) {
        finalStates.add(state);
    }
}
