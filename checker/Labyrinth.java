import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Labyrinth {
    /** checks if there is a wall in the given orientation */
    public static void checkForWall(int labyrinthCodification,
                               int orientation,
                               int currentNode,
                               int neighbor,
                               Automata automata) {
        int hasNoWall = labyrinthCodification & orientation;
        if (hasNoWall == 0) {
            // connect to neighbor
            automata.graph.addEdge(currentNode, neighbor);
        } else {
            // connect to itself
            automata.graph.addEdge(currentNode, currentNode);
        }
    }

    /** takes the input from STDIN and converts it into an Automata */
    public static Automata readInput() {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        try {
            String firstLine = reader.readLine();
            String[] numbers = firstLine.split("\\s");

            int l = Integer.parseInt(numbers[0]);
            int c = Integer.parseInt(numbers[1]);
            int r = Integer.parseInt(numbers[2]);
            int o = Integer.parseInt(numbers[3]);

            // the automata will have 4 symbols for: east, north, west and south
            Automata automata = new Automata(l * c, 4, r, o);

            // read labyrinth codification and convert it into graph edges
            for (int i = 0; i < l; ++i) {
                String currentLine = reader.readLine();

                String[] currentNumbers = currentLine.split("\\s");

                for (int j = 0; j < c; ++j) {
                    int currentCodification =
                            Integer.parseInt(currentNumbers[j]);

                    int east = 1, north = 2, west = 4, south = 8;

                    checkForWall(currentCodification,
                                 east,
                                 i * c + j,
                                 i * c + (j + 1),
                                 automata);

                    checkForWall(currentCodification,
                                 north,
                                 i * c + j,
                                 (i - 1) * c + j,
                                 automata);

                    checkForWall(currentCodification,
                                 west,
                                 i * c + j,
                                 i * c + (j - 1),
                                 automata);

                    checkForWall(currentCodification,
                                 south,
                                 i * c + j,
                                 (i + 1) * c + j,
                                 automata);
                }
            }

            // robots positions
            String robotsPositionsLine = reader.readLine();
            String[] robotsPositions = robotsPositionsLine.split("\\s");

            for (int i = 0; i < robotsPositions.length; i += 2) {
                int x = Integer.parseInt(robotsPositions[i]);
                int y = Integer.parseInt(robotsPositions[i + 1]);

                automata.addInitialState(x * c + y);
            }

            // exits positions
            String exitPositionsLine = reader.readLine();
            String[] exitPositions = exitPositionsLine.split("\\s");

            for (int i = 0; i < exitPositions.length; i += 2) {
                int x = Integer.parseInt(exitPositions[i]);
                int y = Integer.parseInt(exitPositions[i + 1]);

                automata.addFinalState(x * c + y);
            }

            return automata;
        } catch (IOException ioe) {
            System.out.println(ioe);

            return null;
        }
    }

    public static void printAutomata(Automata automata) {
        System.out.println(automata.n + " " +
                           automata.m + " " +
                           automata.s + " " +
                           automata.f);

        for (int i = 0; i < automata.n; ++i) {
            for (int state : automata.graph.adjLists[i]) {
                System.out.print(state + " ");
            }
            System.out.println();
        }

        for (int initialState : automata.initialStates) {
            System.out.print(initialState + " ");
        }
        System.out.println();

        for (int finalState : automata.finalStates) {
            System.out.print(finalState + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Automata automata = readInput();

        printAutomata(automata);
    }
}
