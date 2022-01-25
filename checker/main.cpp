#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <algorithm>
#include <queue>
#include <unordered_map>
#include <list>
#include <string.h>

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
    DATA_TYPE nodesCount;

    // bool* visited;
    vector<bool> visited;
    list<DATA_TYPE> *adjLists;

    Graph();
    Graph(DATA_TYPE nodesCount); 

    void DFS(DATA_TYPE currentNode);
    bool BFS(DATA_TYPE startNode, bool* finalStatesBools);
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
    this->nodesCount = nodesCount;

    // this->visited = new bool[nodesCount];

    // for (DATA_TYPE i = 0; i < nodesCount; ++i) {
    //     this->visited[i] = false;
    // }
    vector<bool> visitedNodesBools(nodesCount, false);
    this->visited = visitedNodesBools;

    this->adjLists = new list<DATA_TYPE>[nodesCount];
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
        for (DATA_TYPE j = 0; j < m; ++j) {
            DATA_TYPE destination;

            cin >> destination;

            automataData.graphData.adjLists[i].push_back(destination);
        }
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

    list<DATA_TYPE>::iterator i;
    
    // go through all the node neighbours
    for (i = adjLists[currentNode].begin(); i != adjLists[currentNode].end(); ++i) {
        // explore the non visited ones
        if (!visited[*i])
            DFS(*i);
    }
}

// s != 0, f = 0;
vector<DATA_TYPE> solveAccessibleProblem(Automata automataData)
{
    vector<DATA_TYPE> accessibleStates;

    // run dfs on all initial states
    for (DATA_TYPE i = 0; i < automataData.s; ++i) {
        DATA_TYPE initialState = automataData.initialStates[i];

        if (!automataData.graphData.visited[initialState]) {
            automataData.graphData.DFS(initialState);
        }
    }

    // all the accessible states are found in the visited vector
    for (DATA_TYPE j = 0; j < automataData.n; ++j) {
        if (automataData.graphData.visited[j]) {
            accessibleStates.push_back(j);
        }
    }

    return accessibleStates;
}

bool Graph::BFS(DATA_TYPE startNode, bool* finalStatesBools)
{
    // case for when the starting node itself is a final state
    if (finalStatesBools[startNode]) 
        return true;

    list<DATA_TYPE> queue;

    // mark the starting node as visited and save it in the queue
    visited[startNode] = true;
    queue.push_back(startNode);

    list<DATA_TYPE>::iterator i;
    DATA_TYPE currentNode;

    while(!queue.empty()) {
        // get the first node in the queue
        currentNode = queue.front();
        queue.pop_front();

        if (finalStatesBools[currentNode]) 
            return true;

        // go through all the node neighbours
        for (i = adjLists[currentNode].begin(); i != adjLists[currentNode].end(); ++i) {
            if (!visited[*i]) {
                if (finalStatesBools[*i]) 
                    return true;

                visited[*i] = true;

                queue.push_back(*i);
            }
        }
    }

    return false;
}

/// s = 0, f != 0
vector<DATA_TYPE> solveProductiveProblem(Automata automataData)
{
    vector<DATA_TYPE> productiveStates;
    bool* finalStatesBools = new bool[automataData.n];

    for (DATA_TYPE i = 0; i < automataData.n; ++i)
        finalStatesBools[i] = false;

    for (DATA_TYPE i = 0; i < automataData.f; ++i) 
        finalStatesBools[automataData.finalStates[i]] = true;

    for (DATA_TYPE i = 0; i < automataData.n; ++i) {
        for (DATA_TYPE j = 0; j < automataData.n; ++j) 
            automataData.graphData.visited[j] = false;

        // bool isProductive = automataData.graphData.BFS(i, finalStatesBools);

        // if (isProductive) productiveStates.push_back(i);
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