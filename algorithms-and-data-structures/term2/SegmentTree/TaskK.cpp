#include <vector>
#include <fstream>

using namespace std;

int R, n, m;
struct matrix {
    int r0, r1, r2, r3;
    matrix() {}
    matrix(int a, int b, int c, int d) {
        r0 = a;
        r1 = b;
        r2 = c;
        r3 = d;
    }
};
vector<matrix> a;
vector<matrix> tree;

matrix multiply(matrix a, matrix b) {
    matrix result;
    result.r0 = ((a.r1 * b.r2) + (a.r0 * b.r0)) % R;
    result.r1 = ((a.r1 * b.r3) + (a.r0 * b.r1)) % R;
    result.r2 = ((a.r3 * b.r2) + (a.r2 * b.r0)) % R;
    result.r3 = ((a.r3 * b.r3) + (a.r2 * b.r1)) % R;
    return result;
}

void build(int v, int l, int r)
{
    if (l == r - 1)
    {
        tree[v] = a[l];
        return;
    }
    int mid = l + (r - l) / 2;
    build(2 * v + 1, l, mid);
    build(2 * v + 2, mid, r);
    tree[v] = multiply(tree[2 * v + 1], tree[2 * v + 2]);
}

matrix get_multiply(int v, int l, int r, int a, int b) {
    if (a <= l && r <= b) return tree[v];
    if (l >= b || r <= a) return matrix(1, 0, 0, 1);
    int mid = l + (r - l)/2;
    return multiply(get_multiply(2 * v + 1, l, mid, a, b),
                    get_multiply(2 * v + 2, mid, r, a, b));
}

int main() {
    ifstream in;
    in.open("crypto.in");
    in >> R >> n >> m;

    a = vector<matrix>(n);
    tree = vector<matrix>(4 * n);

    for (int i = 0; i < n; i++) {
        in >> a[i].r0 >> a[i].r1 >> a[i].r2 >> a[i].r3;
    }

    build(0, 0, n);

    int left, right;
    ofstream out;
    out.open("crypto.out");
    for (int i = 0; i < m; i++) {
        in >> left >> right;
        matrix M = get_multiply(0, 0, n, left - 1, right);
        out << M.r0 << " " << M.r1 << "\n" << M.r2 << " " << M.r3 << "\n\n";
    }
    in.close();
    out.close();
    return 0;
}