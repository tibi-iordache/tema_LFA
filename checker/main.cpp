#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <algorithm>
#include <queue>
#include <unordered_map>
#include <list>

using namespace std;

typedef unsigned short int DATA_TYPE;

enum ProblemTypes {
    ACCESSIBLE,
    PRODUCTIVE,
    UTILS,
    SYNCHRONIZE
};

class Graph {
public:
    map<DATA_TYPE, bool> visited;
    // TODO: explain this
    map<DATA_TYPE, vector<DATA_TYPE>> adj;

    
    // TODO: remove later if useless
    // for bfs
    DATA_TYPE V;
    list<DATA_TYPE> *adj_bfs;

    Graph();
    Graph(DATA_TYPE V); // constructor

    void DFS(DATA_TYPE currentNode);
    bool* BFS(DATA_TYPE startNode);
};

class Automata {
public: 
    DATA_TYPE n, m, s, f;
    Graph graphData;
    vector<DATA_TYPE> initialStates;
    vector<DATA_TYPE> finalStates;

    Automata(DATA_TYPE n, DATA_TYPE m, DATA_TYPE s, DATA_TYPE f);
};

Graph::Graph() 
{

}

Graph::Graph(DATA_TYPE V) 
{
    this->V = V;
    adj_bfs = new list<DATA_TYPE>[V];
}

Automata::Automata(DATA_TYPE n, DATA_TYPE m, DATA_TYPE s, DATA_TYPE f)
{
    this->n = n;
    this->m = m;
    this->s = s;
    this->f = f;

    Graph g(n);
    this->graphData = g;
}


Automata readInput()
{
    DATA_TYPE n, m, s, f;

    // read the automata details
    cin >> n 
        >> m
        >> s
        >> f;

    Automata automataData(n, m, s, f);

    // read the graph adj lists
    for (DATA_TYPE i = 0; i < n; ++i) {
        vector<DATA_TYPE> destinations;

        for (DATA_TYPE j = 0; j < m; ++j) {
            DATA_TYPE destination;

            cin >> destination;

            destinations.push_back(destination);

            // for bfs
            automataData.graphData.adj_bfs[i].push_back(destination);
        }

        automataData.graphData.adj[i] = destinations;
    }

    // read the initial states
    for (DATA_TYPE i = 0; i < s; ++i) {
        DATA_TYPE initialState;

        cin >> initialState;

        automataData.initialStates.push_back(initialState);
    }

    // read the final states
    for (DATA_TYPE i = 0; i < f; ++i) {
        DATA_TYPE finalState;

        cin >> finalState;

        automataData.finalStates.push_back(finalState);
    }

    return automataData;
}


void Graph::DFS(DATA_TYPE currentNode)
{
    // print the current node and mark it as visited
    visited[currentNode] = true;
    
    // go through all the node neighbours
    for (DATA_TYPE i = 0; i < adj[currentNode].size(); ++i) {
        // explore the non visited ones
        if (!visited[adj[currentNode][i]])
            DFS(adj[currentNode][i]);
    }
}

// s != 0, f = 0;
vector<DATA_TYPE> solveAccessibleProblem(Automata automataData)
{
    vector<DATA_TYPE> accessibleStates;

    for (DATA_TYPE i = 0; i < automataData.s; ++i) {
        DATA_TYPE initialState = automataData.initialStates[i];

        if (!automataData.graphData.visited[i]) {
            automataData.graphData.DFS(i);

            map<DATA_TYPE, bool>::iterator j;

            for (j = automataData.graphData.visited.begin();
                 j != automataData.graphData.visited.end();
                 ++j) {
                     if ((*j).second) {
                        accessibleStates.push_back((*j).first);
                     }
                 }
        }
    }

    return accessibleStates;
}

bool* Graph::BFS(DATA_TYPE startNode)
{
    bool *visitedNodes = new bool[V];
    for(DATA_TYPE i = 0; i < V; ++i)
        visitedNodes[i] = false;

    list<DATA_TYPE> queue;

    // mark the starting node as visited and save it in the queue
    visitedNodes[startNode] = true;
    queue.push_back(startNode);

    list<DATA_TYPE>::iterator i;
    DATA_TYPE currentNode;

    while(!queue.empty()) {
        // get the first node in the queue
        currentNode = queue.front();
        queue.pop_front();

        // check all the neighbours
        for (i = adj_bfs[currentNode].begin(); i != adj_bfs[currentNode].end(); ++i) {
            if (!visitedNodes[*i]) {
                visitedNodes[*i] = true;

                queue.push_back(*i);
            }
        }
    }

    return visitedNodes;
}

/// s = 0, f != 0
vector<DATA_TYPE> solveProductiveProblem(Automata automataData)
{
    vector<DATA_TYPE> productiveStates;

    
    for (DATA_TYPE i = 0; i < automataData.n; ++i) {
        bool* visitedNodes = automataData.graphData.BFS(i);

        vector<DATA_TYPE>::iterator j;
        vector<DATA_TYPE>::reverse_iterator k;

        for (j = automataData.finalStates.begin(), k = automataData.finalStates.rbegin();
             j != automataData.finalStates.end() && k != automataData.finalStates.rend();
             ++j, ++k) {
            if (visitedNodes[*j] || visitedNodes[*k]) {
                productiveStates.push_back(i);

                break;
            } 
        }
    }

    return productiveStates;
}

vector<DATA_TYPE> vectorIntersection(vector<DATA_TYPE> &v1, 
                                     vector<DATA_TYPE> &v2)
{
    vector<DATA_TYPE> v3;

    sort(v1.begin(), v1.end());
    sort(v2.begin(), v2.end());

    set_intersection(v1.begin(), v1.end(),
                     v2.begin(), v2.end(),
                     back_inserter(v3));

    return v3;
}                                    

// s != 0, f != 0
vector<DATA_TYPE> solveUtilsProblem(Automata automataData)
{
    vector<DATA_TYPE> accessibleStates = solveAccessibleProblem(automataData);

    vector<DATA_TYPE> productiveStates = solveProductiveProblem(automataData);

    return vectorIntersection(accessibleStates, productiveStates);
}

int main(int argc, char *argv[]) {
    string problemType(argv[1]);

    map<string, ProblemTypes> problemTypesMap;
    problemTypesMap["accessbile"] = ACCESSIBLE;
    problemTypesMap["productive"] = PRODUCTIVE;
    problemTypesMap["useful"] = UTILS;
    problemTypesMap["synchronize"] = SYNCHRONIZE;

    // read input from STDIN
    Automata automataData = readInput();

    switch (problemTypesMap[problemType]) {
        case ACCESSIBLE: {
            vector<DATA_TYPE> accessibleStates = solveAccessibleProblem(automataData);

            vector<DATA_TYPE>::iterator i;
            for (i = accessibleStates.begin(); i != accessibleStates.end(); ++i) {
                cout << *i << endl;
            }

            break;
        }

        case PRODUCTIVE: {
            vector<DATA_TYPE> productiveStates = solveProductiveProblem(automataData);

            vector<DATA_TYPE>::iterator i;
            for (i = productiveStates.begin(); i != productiveStates.end(); ++i) {
                cout << *i << endl;
            }

            break;
        }

        case UTILS: {
            vector<DATA_TYPE> utilStates = solveUtilsProblem(automataData);

            vector<DATA_TYPE>::iterator i;
            for (i = utilStates.begin(); i != utilStates.end(); ++i) {
                cout << *i << endl;
            }

            break;
        }

        case SYNCHRONIZE: {
            // TODO:

            break;
        }
            
    
        default:
            break;
    }

    return 0;
}