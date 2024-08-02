#include <set>
#include <fstream>

using namespace std;

int parking(set<int> &collection, int x, int place) {
    auto emp = collection.lower_bound(x);
    if (emp == collection.end())
    {
        place = *collection.begin();
    }
    else
    {
        place = *emp;
    }
    collection.erase(place);
    return place;
}

signed main() {
    ifstream in;
    in.open("parking.in");
    set<int> collection;
    int n, m, x;
    in >> n >> m;

    for (int i = 1; i <= n; i++)
    {
        collection.insert(i);
    }

    string com;
    int place;
    ofstream out;
    out.open("parking.out");

    for (int i = 0; i < m; i++)
    {
        in >> com >> x;
        if (com == "exit")
        {
            collection.insert(x);
        }
        else
        {
            out << parking(collection, x, place) << endl;
        }
    }
    in.close();
    out.close();
    return 0;
}