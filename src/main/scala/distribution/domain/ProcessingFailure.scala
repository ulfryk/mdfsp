package distribution.domain

/**
 * It's merely a seed of an exception that will wrap all our internal failures describing unhappy paths.
 */
class ProcessingFailure() extends RuntimeException("ProcessingFailure")
