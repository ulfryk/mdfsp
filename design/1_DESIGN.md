This is a track of a simplified domain design process.
Sticking to DDD principles as much as possible in a one-person task-force.

---

# Strategic Phase

Obviously simplified - can't be really done properly in a one-person team :)
I'll assume that provided requirements are already an output of a few event
storming rounds.

## Initial thoughts and questions
<a id="initial-thoughts-and-questions"></a>

Based on provided *Task Requirements*.

1. _artist adds songs to a release;_
    1. songs are music files in decent quality, at least 44 khz uncompressed audio, decent upload functionality is
       needed
    2. songs have names
    3. there must be some kind of user/actor representation in the system to perform this action (and ui)
    4. does the release have a name?
2. _artist proposes a release date;_
    1. not required when creating release (most probably)
    2. there must be some kind of user/actor representation in the system to perform this action (and ui)
3. _proposed release date has to be agreed by record label (unlabeled artists are out of scope);_
    1. there must be some kind of user/actor linked to the record label
    2. can it be updated by artist after approval?
    3. can artist add songs after an approval by the record label?
    4. can artist have releases with different record labels?
4. _only when release date is agreed and reached, then songs from release are distributed for streaming;_
    1. some kind of scheduler may be required (maybe not if streaming platforms support release date)
    2. (!) can the release or its songs be changed after the release date is agreed by the record label?
    3. how fast songs/releases propagate on streaming services? (shouldn't we deliver them in advance?)
    4. can a release or a song be updated on a streaming service?
5. _released songs can be searched by title using Levenshtein distance algorithm;_
    1. this requirement is pretty unclear, should they be searched in the context of release, artist or globally?
    2. who can do this, artists team member or record label representative or both? (or some backoffice support team)
    3. should it be implemented in-house, or can we use external libs or persistence engines' features?
6. _keep track of streamed released songs for the distribution — only stream longer than 30sec is considered
   for monetization (assume streams are unique between each other);_
    1. should we constantly pull info about streams from a streaming platform, or do we do it in batches periodically?
7. _artist can request a report of streamed songs — both monetized and not;_
    1. is it something visualised in ui or a file? If the latter one, what is/are the format/s?
    2. should it contain a list of all streams with dates and times of streaming or only list of songs
       with stream counts (monetised vs. unmonetised)?
    3. should there be any filtering capability (date range, single release, single record label)?
    4. what info should it have? what columns? song id or song name, should it contain label and/or release ref?
8. _artist can file for payment for all monetised streams since last payment;_
    1. (!) what about the record label? shouldn't payment be split between it and artist?
    2. this would require billing details most probably…
    3. do we handle splitting payment to separate artist team members?
    4. should the payment hold info about the range of streams covered?
9. _finally, artist can take out release from distribution meaning that songs cannot be streamed anymore._
    1. how fast streaming platforms remove songs? (does it matter for us?)
    2. can such a release be activated again? would it require approval from the record label?

## Events

### Exactly described in requirements

1. A song was added to the release by an artist
2. A release date was proposed by artist
3. The proposed date was approved by the record label
4. Songs where distributed for streaming
5. …
6. New stream was created
7. …
8. Payment request was created
9. Release and its songs were withdrawn from distribution

Any other events I can think of are listed at the end of this document in [The Future](#the-future) section.

## Domains

Based on task requirements, I predict two (or three) domains:

- music distribution (uploading songs, manging releases and artists, and their teams)
- user management/identity (all things about artist team members and record label representatives access to perform
  actions)
- (?) payments/wallet (all things about getting payments by artist)

The first one is a core domain, the second one is a generic domain that can be easily outsourced (i.e. OAuth2).
The last one (a subdomain) is just a mare idea of how it may evolve in a real world scenario. I'll ignore it for
the sake of simplicity and keep a simplified Payment Request thing inside the music distribution.

In further development, there can be another support domain extracted from the core domain (it's an opinion, prediction):

- organisation / operations - responsible for management of artists, record labels and their team members

So we have:

- `Music Distribution` (core, implemented here)
- `Identity` (generic, out of scope of the task)

## Context mapping

Not much happening here, the only relation is `Team Member` from `Music Distribution` being backed by a user from
`Identity`. It creates some questions and issues to solve (Team Member's lifecycle, when is it matched with a
user, etc.), yet they all don't need to be addressed now. For the sake of MVP prototype, I assume that Artist, Team
Member, Record Label all already exist, so I don't need to investigate and implement any logic around them.

## Language

I assume the virtual team agreed on these terms: Artist, Record Label, organisation, Platform User, Team Member,
Release, Song, Stream, Wallet, Payment Request, and a Streaming Report.

---

# Tactical Phase

## Entities

- Artist
    - id
    - name
- Record Label
    - id
    - name
- Platform User (a bridge between some identity provider and our bounded context)
    - id
    - name
- Team Member (or separate Record Label Representative vs. Artist Team Member)
    - id
    - user id
    - organisation id (artist id | record label id)
- Release
    - id
    - artist id
    - label id
    - title
    - release date (irrespective of the timezone)
    - state:
        - ~~CREATED/PENDING~~ `Created`
        - ~~DATE_ASSIGNED~~ `Proposed`
        - ~~DATE_AGREED/READY~~ `Approved`
        - ~~SUSPENDED/CLOSED~~ `Withdrawn`
- Song
    - id
    - release id
    - title
    - ~~music file~~ (out of task's scope)
- Stream
    - id
    - song id
    - duration
    - started at (or maybe _started date_ for consistency?)
    - (?) streamingServiceId (?) or some more metadata (?)
- Payment Request
    - id
    - artist id
    - requested at

## Aggregates

### Artist Aggregate

- root: `Artist`
- contained entities: -
- responsibility: identity?

### Record Label Aggregate

- root: `Record Label`
- contained entities: -
- responsibility: identity?

### Team Member Aggregate

- root: `Team Member`
- contained entities:
    - ~~`Platfrom User`~~ (figured out during implementation that it doesn't provide any value)
- responsibility:
    - providing context for release management

### Release Aggregate

- root: `Release`
- contained entities:
    - list of `Song`
- responsibility:
    - manage songs within release
    - handle release date proposal and approval
    - transition through states `Created` -> `Proposed` -> `Approved` -> `Withdrawn`
- relationship:
    - reference to `Artist`
    - reference to `Record Label`

### Stream Aggregate

- root: `Stream`
- contained entities: -
- responsibility:
    - determine if it's monetised (time > 30s)
- relationship:
    - reference to `Song`

### Payment Request Aggregate

- root: `Payment Request`
- contained entities: -
- responsibility:
    - process payouts for monetised streams
    - track payout history for each artist
- relationship:
    - reference to `Artist`

## Other

### Streaming Report

In my opinion, it doesn't fit any of DDD basic building blocks. It's not a managed entity nor aggregate. It's just
a view of existing data. A list of `Song Streaming Summary` objects.

Proposed fields of `Song Streaming Summary` object (aka columns of Streaming Report):
- song name
- release name
- monetized streams count
- unmonetized streams count

## Assumptions

Addressing here questions from [Initial thoughts and questions](#initial-thoughts-and-questions)

Some decisions are already reflected in previous sections, putting them all here, so it's straightforward to find them in one place.

- re `1.1`: music files quality and upload — ignoring it for sake of MVP
- re `1.3`, `2.2`, `3.1`: user/actor — providing `Platfrom User` and `Team Member` entities
- re `1.4`: release should have a name, or, more accurately, a *title*
- re `2.1`: release date is not provided when creating release but can be set/updated any time before approval
- re `3.2`: for sake of task/MVP the release date cannot be changed after approval (in a real world scenario
  it most probably should be possible)
- re `3.3`: same as previous one — no new songs after approval from the record label (for sake of MVP)
- re `3.4` (multiple rec labels for one artist): Common sense (and my personal experience) tells me that older releases
  can be labeled by a different record company. This, like most here, would be normally resolved with domain experts
  and other stakeholders, but for the sake of this task I will stick to many-labels-per-artist model.
- re `4.1`: there may be a scheduler (to push release to a streaming service), or this action will
  be done in advance (given streaming service supports release date), or maybe something else…
  I'm making an assumption that for sake of MVP we need to provide only a basic logic supporting the push of the release
  to a streaming service(s) ignoring for now the problem of how it will be triggered.
- re `4.2`: same as `3.2` and `3.3` - no changes after approval
- re `4.3`: not a decision to be made now, see the assumption for `4.1` above
- re `4.4`: updating release on streaming service is out of scope of this task
- re `5.1`: I think that songs should be searchable in three contexts (listed below), but skipping it for sake of MVP:
    - record label: record label's team member can search all songs of their releases
    - artist: artist's team member can search all their songs
    - release: any team member can search through songs in a specific release
      (release that references team member's artist or label)
- re `5.2`: I'll assume that any team member can search songs. But I may not require having a decision here for
  the sake of my solution at all.
- re `5.3` (Levenshtein distance impl): It's provided by many persistence engines (like PostgreSQL, MongoDB Atlas,
  Aurora), we can also use ElasticSearch paired with our persistence. Yet I'm not using any of these here, so I can
  either use some library for my fake implementation of persistence or implement one myself. It triggered my curiosity,
  so I'll go with implementing one :)
- re `6.1`: We don't need to know how often the streaming data is synchronised with streaming services it's enough
  for our prototype to accept info about new streams — I'll prepare logic for adding streams one by one. If we have
  batch processing of this data in future, the output can be used as buffer to feed our solution, or we can extend to
  functionality to accept bulk updates.
- re `7.1` (report format): No need to answer this question for now. (My weak prediction is to start with a simple CSV…)
- re `7.2`: I'll assume that for initial solution just stream counts are enough.
- re `7.3` (report filtering): A must-have in a full-blown solution, but I assume it's unnecessary in the MVP.
- re `7.4` (report fields): I'll assume that it's just an informative thing for an artist, so I'll put there only song
  name and release name (besides the stream counts).
- re `8.1`: For simplicity I'll follow an assumption that artist takes out all money, yet I'm strongly convinced that
  this is not a real world scenario.
- re `8.2`: Not thinking about billing details in scope of this task. Let's assume that we just send amount and artist
  id to another service described by supporting domain.
- re `8.3`: Again, let's assume that there is another team resolving this problem ;)
- re `8.4`: Not sure if the payment request should reference streams, but I think it would be good for sake of
  consistency… to resolve later.
- re `9.1`: For sake of this task we don't need to know how fast streaming services take out songs/releases. Having a
  logic to do it is all we need for now.
- re `9.2`: My personal experience tells me that we should be able to re-release a release that was previously withdrawn
  from distribution (so `Suspend` would be better word than `Withdraw` probably), but for sake of MVP prototype's
  simplicity I'll assume that release is terminally out once withdrawn.

---

# Implementation

DDD was invented and later explored with OOP in mind, and it promotes rich model and mutable aggregate and entity
objects. But that's not the only way to approach implementation of solutions designed with DDD. I'm proposing here the
use of domain services (thinking about them as modules grouping functions) covering responsibilities of corresponding
aggregates.

This is a prototype of an MVP, so we are not sure what will be used to store data or communicate with clients and other
services, so I'll use elements of hexagonal architecture here. If it evolves to a real-life project, the team could
decide what technology should be used to store data and implement api and prepare appropriate adapters to the ports.

Taking into consideration ease of predicted system growth, I decided to divide it early into three
submodules: `distribution`, `organisation`, and `wallet`. The Team can maintain isolation between those until it's the
right time to split. Any two of them can be easily merged in case it turns out that initial division didn't make sense.

## Problems

1. Proposed shape of the `Release` entity is somehow ambiguous. Optional `releaseDate` together with `state` allow invalid object state.
2. Creating new `Payment Request` based on a timestamp may cause skipped or double-counted streams.

### Ambiguous model

Using optional date, we can end up with an object in `Released` state but without a date assigned. It's beneficial
to make sure we are not allowed to introduce invalid state at type system level.

My idea for a solution is to create a value object containing both information - state and date, using a sum type
solves it perfectly: `type ReleaseState = Created | Proposed Date | Approved Date | Withdrawn`.

### Timestamp issue

Using timestamp is not the best idea, because:

- Late delivery of data from a streaming platform can put arriving streams in the period already monetized
  and paid out (if we use dates provided by that platform).
- Our own timestamp, assigned when we save it in persistence, can be a bit better. However, still there can be
  a couple of items with exactly the same timestamp, but committing to the database was slower for some, and
  they were not counted for monetization (race condition).
- We can mark all streams in scope as _included in payout_… but it's neither elegant nor effective. With the amounts 
  of stream reaching hundreds of thousands, millions or even more we will shortly hit a bottleneck.

My solution for that is to introduce a strictly monotonic identifier, namely Sequence ID.
This way, there is no ambiguity, no risk of race conditions and no potential performance issue.
One issue we may have at some point is an overflow of the numeric value, which is 9,223,372,036,854,775,807 for
long int in Java. I don't think it is a problem to solve right now, but just in case, I can propose
making it unsigned (it doubles the number of values we can use) and/or use 128 bits…

---

# The Future
<a id="the-future"></a>

## Example Events
The full list of my ideas seems indefinite, so writing barely a few initial ones.

- Release was added by an artist
- Release details where updated by an artist
- Streaming Platform was added to the release
- Payout was initiated based on Payment Request
- Team member was added by an artist/label
- etc.

## Functionalities

- Stream details — streaming provider identifier (Distribution module/subdomain)
- Monetization rules — how single monetized stream translates to real money (Wallet module/subdomain)
- Payout processing on top of Payment Requests — currency, billing details, etc. (Wallet module/subdomain)
- Team Member management — adding, removing, responsibilities (Organisation module/subdomain)
- Streaming Platforms management — where should the release be distributed to (Distribution module/subdomain)
