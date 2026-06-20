'use client';

import { useEffect, useState } from 'react'

export default function Home() {
  const [counter, setCounter] = useState(0);

  useEffect(() => { getCounter().then((i) => setCounter(i)); });

  const handleClick = () => {
    incrementCounter().then((i) => setCounter(i));
  }

  return (
    <main>
      <div>{counter}</div>
      <button onClick={handleClick}>Increment</button>
    </main>
  )
}

async function getCounter() {
  const r = await fetch("/api/counter");
  console.log(r.text);
  const body = await r.json();
  return body.count;
}

async function incrementCounter() {
  const r = await fetch("/api/counter", { method: "POST" });
  console.log(r.text);
  const body = await r.json();
  return body.count;
}