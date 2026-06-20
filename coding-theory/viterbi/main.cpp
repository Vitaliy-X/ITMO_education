#include <algorithm>
#include <cmath>
#include <fstream>
#include <iomanip>
#include <iostream>
#include <limits>
#include <random>
#include <sstream>
#include <string>
#include <vector>

using std::string;
using std::uint32_t;
using std::uint64_t;
using std::vector;

static int bit_parity64(uint64_t x)
{
    x ^= x >> 32;
    x ^= x >> 16;
    x ^= x >> 8;
    x ^= x >> 4;
    x &= 0xFULL;
    return (0x6996 >> x) & 1;
}

static int bit_ctz64(uint64_t x)
{
#if defined(__clang__) || defined(__GNUC__)
    return __builtin_ctzll(x);
#else
    int c = 0;
    while ((x & 1ULL) == 0ULL)
    {
        x >>= 1;
        ++c;
    }
    return c;
#endif
}

static int bit_clz64(uint64_t x)
{
#if defined(__clang__) || defined(__GNUC__)
    return __builtin_clzll(x);
#else
    int c = 0;
    uint64_t m = 1ULL << 63;
    while ((x & m) == 0ULL)
    {
        m >>= 1;
        ++c;
    }
    return c;
#endif
}

static string pow2_str(int d)
{
    string s = "1";
    for (int i = 0; i < d; ++i)
    {
        int carry = 0;
        for (int j = (int) s.size() - 1; j >= 0; --j)
        {
            int v = (s[(size_t) j] - '0') * 2 + carry;
            s[(size_t) j] = char('0' + (v % 10));
            carry = v / 10;
        }
        if (carry)
        {
            s.insert(s.begin(), char('0' + carry));
        }
    }
    return s;
}

static string sci2(double x)
{
    if (!std::isfinite(x))
    {
        return "nan";
    }

    std::ostringstream oss;
    oss.setf(std::ios::scientific);
    oss << std::uppercase << std::setprecision(2) << x;

    string s = oss.str();
    auto p = s.find('E');
    if (p == string::npos)
    {
        return s;
    }

    string mant = s.substr(0, p);
    int e = 0;
    try
    {
        e = std::stoi(s.substr(p + 1));
    }
    catch (...)
    {
        return s;
    }

    return mant + "E" + std::to_string(e);
}

struct Row
{
    int n = 0;
    int wcnt = 0;
    vector<uint64_t> w;

    Row() = default;

    explicit Row(int n_) : n(n_)
    {
        wcnt = (n + 63) / 64;
        w.assign((size_t) wcnt, 0ULL);
    }

    [[nodiscard]] bool get(int i) const
    {
        return (w[(size_t) (i >> 6)] >> (i & 63)) & 1ULL;
    }

    void set(int i, bool v)
    {
        uint64_t &x = w[(size_t) (i >> 6)];
        uint64_t m = 1ULL << (i & 63);
        if (v)
        {
            x |= m;
        }
        else
        {
            x &= ~m;
        }
    }

    void reset()
    {
        std::fill(w.begin(), w.end(), 0ULL);
    }

    void xor_with(const Row &other)
    {
        for (int i = 0; i < wcnt; ++i)
        {
            w[(size_t) i] ^= other.w[(size_t) i];
        }
    }

    [[nodiscard]] bool zero() const
    {
        for (uint64_t x : w)
        {
            if (x)
            {
                return false;
            }
        }
        return true;
    }

    [[nodiscard]] int first1() const
    {
        for (int i = 0; i < wcnt; ++i)
        {
            uint64_t x = w[(size_t) i];
            if (!x)
            {
                continue;
            }
            return i * 64 + bit_ctz64(x);
        }
        return n;
    }

    [[nodiscard]] int last1() const
    {
        for (int i = wcnt - 1; i >= 0; --i)
        {
            uint64_t x = w[(size_t) i];
            if (!x)
            {
                continue;
            }
            return i * 64 + (63 - bit_clz64(x));
        }
        return -1;
    }
};

struct Rng
{
    std::mt19937 mt;

    explicit Rng(uint32_t seed) : mt(seed) {}

    uint32_t u32()
    {
        return mt();
    }

    double u01()
    {
        uint64_t x = (uint64_t) u32() << 32 | (uint64_t) u32();
        uint64_t mant = x >> 11;
        return (double) mant * (1.0 / 9007199254740992.0);
    }

    double n01()
    {
        static const double PI2 = 2.0 * std::acos(-1.0);
        double u1 = u01();
        if (u1 <= 0.0)
        {
            u1 = std::numeric_limits<double>::min();
        }
        double u2 = u01();
        return std::sqrt(-2.0 * std::log(u1)) * std::cos(PI2 * u2);
    }
};

static bool to_msf(vector<Row> &a, vector<int> &L, vector<int> &R)
{
    const int k = static_cast<int>(a.size());
    if (k <= 0)
    {
        return false;
    }
    const int n = a[0].n;

    L.resize(static_cast<size_t>(k));
    R.resize(static_cast<size_t>(k));
    for (int i = 0; i < k; ++i)
    {
        L[(size_t) i] = a[(size_t) i].first1();
        R[(size_t) i] = a[(size_t) i].last1();
    }

    for (int col = 0; col < n; ++col)
    {
        vector<int> same;
        for (int r = 0; r < k; ++r)
        {
            if (L[(size_t) r] == col)
            {
                same.push_back(r);
            }
        }
        if (same.size() <= 1)
        {
            continue;
        }

        int piv = same[0];
        for (int r : same)
        {
            if (R[(size_t) r] < R[(size_t) piv])
            {
                piv = r;
            }
        }

        for (int r : same)
        {
            if (r == piv)
            {
                continue;
            }
            a[(size_t) r].xor_with(a[(size_t) piv]);
            L[(size_t) r] = a[(size_t) r].first1();
            R[(size_t) r] = a[(size_t) r].last1();
            if (a[(size_t) r].zero())
            {
                return false;
            }
        }
    }

    for (int col = n - 1; col >= 0; --col)
    {
        vector<int> same;
        for (int r = 0; r < k; ++r)
        {
            if (R[(size_t) r] == col)
            {
                same.push_back(r);
            }
        }
        if (same.size() <= 1)
        {
            continue;
        }

        int piv = same[0];
        for (int r : same)
        {
            if (L[(size_t) r] > L[(size_t) piv])
            {
                piv = r;
            }
        }

        for (int r : same)
        {
            if (r == piv)
            {
                continue;
            }
            a[(size_t) r].xor_with(a[(size_t) piv]);
            L[(size_t) r] = a[(size_t) r].first1();
            R[(size_t) r] = a[(size_t) r].last1();
            if (a[(size_t) r].zero())
            {
                return false;
            }
        }
    }

    vector<int> seenL((size_t) n + 1, 0), seenR((size_t) n, 0);
    for (int i = 0; i < k; ++i)
    {
        int l = L[(size_t) i], r = R[(size_t) i];
        if (l < 0 || l >= n)
        {
            return false;
        }
        if (r < 0 || r >= n)
        {
            return false;
        }
        if (++seenL[(size_t) l] > 1)
        {
            return false;
        }
        if (++seenR[(size_t) r] > 1)
        {
            return false;
        }
    }
    return true;
}

struct Layer
{
    int a = 0, an = 0, s = 0;
    size_t sN = 0;
    uint64_t mA = 0, mS = 0;
    vector<uint32_t> go;
    vector<uint8_t> bit;
    vector<uint8_t> from;
    vector<uint8_t> pos;
};

struct Graph
{
    int n = 0, k = 0;
    vector<int> dim;
    vector<Layer> lay;
    size_t maxStates = 1;
};

static bool make_graph(const vector<Row> &msf, const vector<int> &L, const vector<int> &R, Graph &g)
{
    const int k = (int) msf.size();
    const int n = msf[0].n;
    g.n = n;
    g.k = k;

    vector<vector<int>> start((size_t) n), act((size_t) n + 1);
    for (int r = 0; r < k; ++r)
    {
        start[(size_t) L[(size_t) r]].push_back(r);
    }

    for (int i = 0; i <= n; ++i)
    {
        for (int r = 0; r < k; ++r)
        {
            if (L[(size_t) r] < i && R[(size_t) r] >= i)
            {
                act[(size_t) i].push_back(r);
            }
        }

        std::sort(act[(size_t) i].begin(), act[(size_t) i].end(), [&](int x, int y) {
            if (L[(size_t) x] != L[(size_t) y])
            {
                return L[(size_t) x] < L[(size_t) y];
            }
            return x < y;
        });

        if ((int) act[(size_t) i].size() > 60)
        {
            return false;
        }
    }

    for (int i = 0; i < n; ++i)
    {
        std::sort(start[(size_t) i].begin(), start[(size_t) i].end(), [&](int x, int y) {
            if (R[(size_t) x] != R[(size_t) y])
            {
                return R[(size_t) x] < R[(size_t) y];
            }
            return x < y;
        });

        if ((int) start[(size_t) i].size() > 60)
        {
            return false;
        }
    }

    g.dim.assign((size_t) n + 1, 0);
    int maxD = 0;
    uint64_t sum = 0;
    for (int i = 0; i <= n; ++i)
    {
        g.dim[(size_t) i] = (int) act[(size_t) i].size();
        if (g.dim[(size_t) i] > 30)
        {
            return false;
        }
        maxD = std::max(maxD, g.dim[(size_t) i]);
        if (i > 0)
        {
            sum += (uint64_t) 1ULL << (unsigned) g.dim[(size_t) i];
        }
    }

    if (sum > 20000000ULL)
    {
        return false;
    }

    g.maxStates = (size_t) 1ULL << (unsigned) maxD;

    g.lay.resize((size_t) n);
    vector<int> inA((size_t) k, -1), inS((size_t) k, -1);

    for (int i = 0; i < n; ++i)
    {
        const auto &A = act[(size_t) i];
        const auto &B = act[(size_t) i + 1];
        const auto &S = start[(size_t) i];

        int a = (int) A.size(), an = (int) B.size(), s = (int) S.size();
        if (a > 30 || an > 30 || s > 30)
        {
            return false;
        }
        if (a + s > 30)
        {
            return false;
        }

        std::fill(inA.begin(), inA.end(), -1);
        std::fill(inS.begin(), inS.end(), -1);
        for (int j = 0; j < a; ++j)
        {
            inA[(size_t) A[(size_t) j]] = j;
        }
        for (int j = 0; j < s; ++j)
        {
            inS[(size_t) S[(size_t) j]] = j;
        }

        Layer lay;
        lay.a = a;
        lay.an = an;
        lay.s = s;
        lay.sN = (size_t) 1ULL << (unsigned) s;

        uint64_t mA = 0, mS = 0;
        for (int j = 0; j < a; ++j)
        {
            if (msf[(size_t) A[(size_t) j]].get(i))
            {
                mA |= (1ULL << j);
            }
        }
        for (int j = 0; j < s; ++j)
        {
            if (msf[(size_t) S[(size_t) j]].get(i))
            {
                mS |= (1ULL << j);
            }
        }
        lay.mA = mA;
        lay.mS = mS;

        lay.from.assign((size_t) an, 0);
        lay.pos.assign((size_t) an, 0);
        for (int j = 0; j < an; ++j)
        {
            int r = B[(size_t) j];
            if (inA[(size_t) r] != -1)
            {
                lay.from[(size_t) j] = 0;
                lay.pos[(size_t) j] = (uint8_t) inA[(size_t) r];
            }
            else
            {
                if (inS[(size_t) r] == -1)
                {
                    return false;
                }
                lay.from[(size_t) j] = 1;
                lay.pos[(size_t) j] = (uint8_t) inS[(size_t) r];
            }
        }

        size_t prevN = (size_t) 1ULL << (unsigned) a;
        size_t sN = (size_t) 1ULL << (unsigned) s;
        size_t total = prevN * sN;

        lay.go.assign(total, 0);
        lay.bit.assign(total, 0);

        for (size_t pm = 0; pm < prevN; ++pm)
        {
            size_t base = pm * sN;
            for (size_t sm = 0; sm < sN; ++sm)
            {
                auto b = (uint8_t) ((bit_parity64((uint64_t) pm & lay.mA) ^ bit_parity64((uint64_t) sm & lay.mS)) & 1);
                uint64_t nm = 0;
                for (int j = 0; j < an; ++j)
                {
                    uint64_t v = (lay.from[(size_t) j] == 0)
                                     ? (((uint64_t) pm >> lay.pos[(size_t) j]) & 1ULL)
                                     : (((uint64_t) sm >> lay.pos[(size_t) j]) & 1ULL);
                    nm |= (v << j);
                }
                lay.go[base + sm] = (uint32_t) nm;
                lay.bit[base + sm] = b;
            }
        }

        g.lay[(size_t) i] = std::move(lay);
    }

    return true;
}

struct Solver
{
    Graph g;
    vector<double> dp0, dp1;
    vector<vector<uint32_t>> pstate;
    vector<vector<uint8_t>> pbit;

    explicit Solver(Graph gg) : g(std::move(gg))
    {
        dp0.assign(g.maxStates, 0.0);
        dp1.assign(g.maxStates, 0.0);
        pstate.resize((size_t) g.n);
        pbit.resize((size_t) g.n);
        for (int i = 0; i < g.n; ++i)
        {
            size_t sz = (size_t) 1ULL << (unsigned) g.lay[(size_t) i].an;
            pstate[(size_t) i].assign(sz, 0);
            pbit[(size_t) i].assign(sz, 0);
        }
    }

    bool decode(const vector<double> &llr, vector<uint8_t> &out)
    {
        if ((int) llr.size() != g.n)
        {
            return false;
        }

        const double INF = 1e100;

        vector<double> *cur = &dp0;
        vector<double> *nxt = &dp1;

        size_t curN = (size_t) 1ULL << (unsigned) g.dim[0];
        for (size_t i = 0; i < curN; ++i)
        {
            (*cur)[i] = INF;
        }
        (*cur)[0] = 0.0;

        for (int i = 0; i < g.n; ++i)
        {
            const Layer &lay = g.lay[(size_t) i];
            size_t nxtN = (size_t) 1ULL << (unsigned) lay.an;
            for (size_t s = 0; s < nxtN; ++s)
            {
                (*nxt)[s] = INF;
            }

            size_t sN = lay.sN;
            for (size_t ps = 0; ps < curN; ++ps)
            {
                double baseM = (*cur)[ps];
                if (baseM >= INF / 2)
                {
                    continue;
                }

                size_t base = ps * sN;
                for (size_t sm = 0; sm < sN; ++sm)
                {
                    uint32_t ns = lay.go[base + sm];
                    uint8_t b = lay.bit[base + sm];
                    double add = (b == 0) ? -llr[(size_t) i] : +llr[(size_t) i];
                    double cand = baseM + add;
                    if (cand < (*nxt)[(size_t) ns])
                    {
                        (*nxt)[(size_t) ns] = cand;
                        pstate[(size_t) i][(size_t) ns] = (uint32_t) ps;
                        pbit[(size_t) i][(size_t) ns] = b;
                    }
                }
            }

            std::swap(cur, nxt);
            curN = nxtN;
        }

        if (curN != 1)
        {
            return false;
        }

        out.assign((size_t) g.n, 0);
        uint32_t st = 0;
        for (int i = g.n - 1; i >= 0; --i)
        {
            out[(size_t) i] = pbit[(size_t) i][(size_t) st];
            st = pstate[(size_t) i][(size_t) st];
        }
        return true;
    }
};

static void encode_word(const vector<Row> &G, const vector<uint8_t> &u, Row &tmp, vector<uint8_t> &c)
{
    const int k = (int) G.size();
    const int n = G[0].n;

    tmp.reset();
    for (int i = 0; i < k; ++i)
    {
        if (u[(size_t) i])
        {
            tmp.xor_with(G[(size_t) i]);
        }
    }

    c.resize((size_t) n);
    for (int j = 0; j < n; ++j)
    {
        c[(size_t) j] = tmp.get(j) ? 1 : 0;
    }
}

static string bits_line(const vector<uint8_t> &b)
{
    std::ostringstream oss;
    for (size_t i = 0; i < b.size(); ++i)
    {
        if (i)
        {
            oss << ' ';
        }
        oss << int(b[i]);
    }
    return oss.str();
}

int main()
{
    std::ifstream fin("input.txt");
    std::ofstream fout("output.txt", std::ios::trunc);
    if (!fout)
    {
        return 0;
    }
    if (!fin)
    {
        fout << "FAILURE\n";
        return 0;
    }

    int n, k;
    if (!(fin >> n >> k) || n <= 0 || k <= 0)
    {
        fout << "FAILURE\n";
        return 0;
    }

    vector<Row> G;
    G.reserve((size_t) k);
    for (int i = 0; i < k; ++i)
    {
        Row r(n);
        for (int j = 0; j < n; ++j)
        {
            int x;
            if (!(fin >> x) || (x != 0 && x != 1))
            {
                fout << "FAILURE\n";
                return 0;
            }
            r.set(j, x == 1);
        }
        G.push_back(std::move(r));
    }

    vector<Row> msf = G;
    vector<int> L, R;
    if (!to_msf(msf, L, R))
    {
        fout << "FAILURE\n";
        return 0;
    }

    Graph gr;
    if (!make_graph(msf, L, R, gr))
    {
        fout << "FAILURE\n";
        return 0;
    }

    auto write_line = [&](const string &s) {
        static bool first = true;
        if (!first)
        {
            fout << "\n";
        }
        first = false;
        fout << s;
    };

    {
        std::ostringstream oss;
        for (int i = 0; i <= n; ++i)
        {
            if (i)
            {
                oss << ' ';
            }
            oss << pow2_str(gr.dim[(size_t) i]);
        }
        write_line(oss.str());
    }

    Solver dec(gr);
    Rng rng(1674);

    Row tmp(n);
    vector<uint8_t> u((size_t) k, 0), cw, dw;
    vector<double> llr((size_t) n);

    string cmd;
    while (fin >> cmd)
    {
        if (cmd == "Encode")
        {
            for (int i = 0; i < k; ++i)
            {
                int b;
                if (!(fin >> b) || (b != 0 && b != 1))
                {
                    fout << "\nFAILURE\n";
                    return 0;
                }
                u[(size_t) i] = (uint8_t) b;
            }
            encode_word(G, u, tmp, cw);
            write_line(bits_line(cw));
        }
        else if (cmd == "Decode")
        {
            for (int i = 0; i < n; ++i)
            {
                if (!(fin >> llr[(size_t) i]))
                {
                    fout << "\nFAILURE\n";
                    return 0;
                }
            }
            if (!dec.decode(llr, dw))
            {
                write_line("ERROR");
            }
            else
            {
                write_line(bits_line(dw));
            }
        }
        else if (cmd == "Simulate")
        {
            double snrDb;
            long long iters, limErr;
            if (!(fin >> snrDb >> iters >> limErr) || iters < 0 || limErr < 0)
            {
                fout << "\nFAILURE\n";
                return 0;
            }
            if (iters == 0)
            {
                write_line(sci2(0.0));
                continue;
            }

            double rate = double(k) / double(n);
            double ebn0 = std::pow(10.0, snrDb / 10.0);
            double sig2 = 1.0 / (2.0 * rate * ebn0);
            double sig = std::sqrt(sig2);

            long long it = 0, err = 0;
            while (it < iters && err < limErr)
            {
                for (int i = 0; i < k; ++i)
                {
                    u[(size_t) i] = (uint8_t) (rng.u32() & 1u);
                }

                encode_word(G, u, tmp, cw);

                for (int i = 0; i < n; ++i)
                {
                    double x = (cw[(size_t) i] == 0) ? 1.0 : -1.0;
                    double y = x + sig * rng.n01();
                    llr[(size_t) i] = 2.0 * y / sig2;
                }

                bool ok = dec.decode(llr, dw);
                bool bad = !ok;
                if (ok)
                {
                    for (int i = 0; i < n; ++i)
                    {
                        if (dw[(size_t) i] != cw[(size_t) i])
                        {
                            bad = true;
                            break;
                        }
                    }
                }
                if (bad)
                {
                    ++err;
                }
                ++it;
            }

            double fer = (it == 0) ? 0.0 : double(err) / double(it);
            write_line(sci2(fer));
        }
        else
        {
            fout << "\nFAILURE\n";
            return EXIT_SUCCESS;
        }
    }

    fout << "\n";
    return EXIT_SUCCESS;
}
