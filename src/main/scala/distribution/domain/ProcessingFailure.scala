package distribution.domain

/**
 * It's merely a seed of an exception that will wrap all our internal failures describing unhappy paths.
 * In real life, every part of the domain would have its own set of Failures. And every such set (sealed trait/class)
 * would extend some common trait used as a member of the class below. That way we can distinct bugs and unexpected
 * exceptions from unhappy paths in logic.
 * 
 * Alternatively, we can return `F[Either[OurFailure, T]]` (given `F[_] : MonadThrow`) everywhere for clarity. Yet
 * this can add a lot of clutter to the logic itself…
 */
class ProcessingFailure() extends RuntimeException("ProcessingFailure")
