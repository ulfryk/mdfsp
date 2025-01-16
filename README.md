# music distribution for a streaming platform

## Design

See `./design/` directory to see my process and slides for a short presentation.

From a tech perspective, I went with Scala 3 (which I prefer over 2). I decided to use
the no-curly-braces. But I have no preference here, both ways are fine for me.

Implementation-wise, I tried to keep things consistent, but I consciously decided to
wrap only part of values in domain Value Objects (using opaque types) and some other
not. I did it, so there is an opportunity to compare both approaches.

## Requirements

Installed Java and SBT (which will load a proper version of Scala) locally.

## Usage

You can compile code with `sbt compile`, run it with `sbt run`.
Although little would happen as the application doesn't implement any API yet.
To see all requirements met at the app logic level, please run `sbt test` - it
will run a bunch of unit tests (definitely not a full coverage) and one end-to-end
test which proves that all requirements are met.

## Disclaimer

It's a prototype of MVP, I decided to NOT take care of several things that would
be important for production ready software. Among them:

- fake repositories are definitely not good, data in memory, not safe against concurrency, slow, etc. etc.
  (don't use for anything else than a short demo with a predefined scenario — like my e2e test)
- mocking the clock in tests
- doing operations on persistence separately - probably we should have a `ReaderT` to ensure transactions,
  or even push more logic to repositories (smelly, but effective, tradeoff to consider in case of poor performance)
- provided unit tests are only a small subset of what should be in a real life solution — i.e. I skipped most unhappy paths
- I have decided to use Id or Try in test implementations of services/repos. In real life it will be `IO` or `Kleisli`, or in some cases `Stream` etc.
