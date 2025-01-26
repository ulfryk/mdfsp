Flow
====

## Show release:

```shell
curl -v 'http://localhost:8114/releases/1' \
  -H 'Accept: application/json'
```

---

## 1. A song was added to the release by an artist

```shell
curl -v 'http://localhost:8114/releases/1/songs' -X POST \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  --data-raw '{"title": "hello world"}'
```

## 2. A release date was proposed by artist

```shell
curl -v 'http://localhost:8114/releases/1' -X PATCH \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  --data-raw '{ "UpdateReleaseState": { "state": { "Proposed": { "date":"2025-01-27" } } } }'
```

## 3. The proposed date was approved by the record label

```shell
curl -v 'http://localhost:8114/releases/1' -X PATCH \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  --data-raw '{ "UpdateReleaseState": { "state": { "Approved": { "date":"2025-01-27" } } } }'
```

## 4. Songs where distributed for streaming

Force to distribution (will not happen through public api for sure):

```shell
curl -v 'http://localhost:8114/releases/1/force-distributed' -X POST \
  -H 'Accept: application/json' 
```

## 5. Released songs where searched by title using Levenshtein distance algorithm.

```shell
curl -v 'http://localhost:8114/released-songs?search=xello%20xorld' \
  -H 'Accept: application/json'
```

## 6. New stream was created

```shell
curl -v 'http://localhost:8114/streams' -X POST \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  --data-raw '{ "songId": 6, "duration": 1 }'
```

## 7. Artist requested and received a report of streamed songs (both monetized and not)

```shell
curl -v 'http://localhost:8114/streams-report2' \
  -H 'Accept: application/json'
```
