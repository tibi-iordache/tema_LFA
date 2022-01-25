import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    /** takes the input from STDIN and converts it into an Automata */
    public static Automata readInput() {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        try {
            String firstLine = reader.readLine();
            String[] numbers = firstLine.split("\\s");

            int n = Integer.parseInt(numbers[0]);
            int m = Integer.parseInt(numbers[1]);
            int s = Integer.parseInt(numbers[2]);
            int f = Integer.parseInt(numbers[3]);

            Automata automata = new Automata(n, m, s, f);

            // compute the graph edges
            for (int i = 0; i < n; ++i) {
                String currentLine = reader.readLine();
                String[] currentNumbers = currentLine.split("\\s");

                for (String currentNumber : currentNumbers)
                    automata.graph.addEdge(i, Integer.parseInt(currentNumber));
            }

            // add the initial states, if there are any
            if (s != 0) {
                String initialStatesLine = reader.readLine();
                String[] initialStates = initialStatesLine.split("\\s");

                for (String initialState : initialStates)
                    automata.addInitialState(Integer.parseInt(initialState));
            }

            // add the final states, if there are any
            if (f != 0) {
                String finalStatesLine = reader.readLine();
                String[] finalStates = finalStatesLine.split("\\s");

                for (String finalState : finalStates)
                    automata.addFinalState(Integer.parseInt(finalState));
            }

            return automata;
        } catch (IOException ioe) {
            System.out.println(ioe);

            return null;
        }
    }

    /**
     * Solves the accessible problem by running a DFS from each initial state
     * of the automata and saving the discovered states.
     *
     * s != 0, f = 0
     * */
    public static List<Integer> solveAccessibleProblem(Automata automata) {
        List<Integer> accessibleStates = new ArrayList<>();

        // the vector is initiated by default with all the values on false
        boolean visitedNodes[] = new boolean[automata.n];

        for (int initialState : automata.initialStates)
            if (!visitedNodes[initialState])
                automata.graph.DFS(initialState, visitedNodes);

        // all the accessible states are found in the visited vector
        for (int i = 0; i < automata.n; ++i)
            if (visitedNodes[i])
                accessibleStates.add(i);

        return accessibleStates;
    }

    /**
     * Solves the productive problem by running a BFS from each state of the
     * automata and checking if it reaches a final state.
     *
     * s = 0, f != 0
     * */
    public static List<Integer> solveProductiveProblem(Automata automata) {
        List<Integer> productiveStates = new ArrayList<>();

        // for each state
        for (int i = 0; i < automata.n; ++i) {
            // if it reaches a final state, mark it as productive
            boolean isProductive = automata.graph.BFS(i, automata.finalStates);

            if (isProductive) productiveStates.add(i);
        }

        return productiveStates;
    }

    /**
     * Solves the utils problem by intersecting the results from the accessible
     * and productive problems shown above.
     *
     * s != 0, f != 0
     * */
    public static List<Integer> solveUtilsProblem(Automata automata) {
        List<Integer> accessibleStates = solveAccessibleProblem(automata);
        List<Integer> productiveStates = solveProductiveProblem(automata);

        // intersect the 2 vectors
        List<Integer> usefulStates = new ArrayList<>(accessibleStates);
        usefulStates.retainAll(productiveStates);

        return usefulStates;
    }

    /**
     * Solves the synchronize problem using the algorithm 2:
     * https://www.math.uni.wroc.pl/~kisiel/auto/Homing.pdf
     *
     * Parameters s and f differ depending on the type of synchronization
     * we are computing(general/partial/restricted/restricted partial).
     * */
    public static List<Integer> solveSynchronizeProblem(Automata automata) {
        List<Integer> result = new ArrayList<>();
        List<Integer> initialStates = new ArrayList<>();
        List<Integer> finalStates = new ArrayList<>();

        if (automata.s == 0 && automata.f == 0) {
            // general case
            for (int i = 0; i < automata.n; ++i)
                initialStates.add(i);
        } else if (automata.s != 0 && automata.f == 0) {
            // partial synchronization
            initialStates = automata.initialStates;
        } else if (automata.s == 0 && automata.f != 0) {
            // restricted synchronization
            for (int i = 0; i < automata.n; ++i)
                initialStates.add(i);

            finalStates = automata.finalStates;
        } else {
            // Restricted partial synchronization
            initialStates = automata.initialStates;
            finalStates = automata.finalStates;
        }

        // while we still have states to merge
        while (initialStates.size() > 1) {
            boolean flagFoundSequence = false;
            List<Integer> mergingSequence = new ArrayList<>();

            // take 2 different states s0 and t0
            for (int s0 : initialStates) {
                for (int t0 : initialStates) {
                    if (s0 != t0) {
                        // check if we have a merging sequence
                        mergingSequence = automata.graph.forestBFS(s0,
                                                                   t0,
                                                                   finalStates);

                        if (!mergingSequence.isEmpty()) {
                            flagFoundSequence = true;
                            break;
                        }
                    }
                }

                if (flagFoundSequence) break;
            }

            if (!flagFoundSequence) {
                // merging sequence not found, we return failure(an empty list)
                return mergingSequence;
            }

            // compute the new set of state by applying the newly found merging
            // sequence to all the current states that need to be merged
            initialStates = automata.graph.computeNewStatesSet(initialStates,
                                                               mergingSequence);

            result.addAll(mergingSequence);
        }

        return result;
    }

    public static void main(String[] args) {
        String problemType = args[0];

        Automata automata = readInput();

        if (automata != null) {
            switch (problemType) {
                case "accessible": {
                    List<Integer> accessibleStates =
                            solveAccessibleProblem(automata);

                    for (int accessibleState : accessibleStates)
                        System.out.println(accessibleState);

                    break;
                }

                case "productive" : {
                    List<Integer> productiveStates =
                            solveProductiveProblem(automata);

                    for (int productiveState : productiveStates)
                        System.out.println(productiveState);

                    break;
                }

                case "useful": {
                    List<Integer> usefulStates = solveUtilsProblem(automata);

                    for (int usefulState: usefulStates)
                        System.out.println(usefulState);

                    break;
                }

                case "synchronize": {
                    List<Integer> synchronizeWord =
                            solveSynchronizeProblem(automata);

                    for (int symbol : synchronizeWord)
                        System.out.println(symbol);

                    break;
                }
            }
        }
    }
}
