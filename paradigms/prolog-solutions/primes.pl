
init(M) :- B is floor(sqrt(M)),
    generator_primes(2, B, [], Primes),
    write(Primes).

generator_primes(I, B, Primes, Primes) :- I > B, !.
generator_primes(I, B, Acc, Primes) :-
    (composite(I) ; Acc = [I | NewAcc]),
    I1 is I + 1,
    generator_primes(I1, B, NewAcc, Primes).

prime(2).
prime(3).
prime(N) :- integer(N), N > 3, N mod 2 =\= 0, \+ has_divisor(N, 3).
has_divisor(N, P) :- N mod P =:= 0.
has_divisor(N, P) :- P * P < N, P2 is P + 2, has_divisor(N, P2).

composite(N) :- \+ prime(N).

min_divisor(N, R, R) :- 0 is N mod R, !.
min_divisor(N, R, I) :- I1 is I + 1, min_divisor(N, R, I1).

prime_divisors(1, []) :- !.
prime_divisors(N, K) :- number(N), !, prime_divisors(N, K, 2).
prime_divisors(N, [N], _) :- prime(N), !.
prime_divisors(N, [H | T], I) :- min_divisor(N, H, I),
    N1 is N / H,
    prime_divisors(N1, T, H).

square_divisors(N, D) :- N1 is N * N, prime_divisors(N1, D).