# Information Retrieval

## By Pavlo Pyvovar and Anton Zaliznyi

### Project for Algorithmics (MTAT.03.238) course of University of Tartu

## Features

For details please refer to our paper

- Naive Inverted Index
- Single Pass In-Memory Indexing (SPIMI)
    - Dictionary Compression
    - Posting Lists Compression
- Boolean Search
- Joker (Wildcard) Search
    - Term Permutation Index
    - K-Gram Index

## Data

As data, you can use any **txt** files.
For example, we have downloaded a large collection of books
from [Gutenberg](https://www.gutenberg.org/browse/scores/top). In addition,
we would also
recommend [this](https://www.kaggle.com/datasets/paulrohan2020/huge-books-in-plain-text-for-train-language-models)
Kaggle dataset and the [Book Corpus](https://github.com/soskek/bookcorpus).

### Single Pass In-Memory Indexing (SPIMI) notes

In order to test SPIMI implementation, you can use the following VM options:

- `-Xms` to set the __initial__ memory allocation pool (e.g. `-Xms128m`)
- `-Xmx` to set the __maximum__ memory allocation pool (e.g. `-Xmx256m`)

Thus, the process will start with `-Xms` amount of memory and
will be allowed to use up to `-Xmx` of RAM.

Dictionary and posting lists compression is built into the SPIMI algorithm.

### Indexing and Compression Results

#### Naive Inverted Index (1024MB of RAM)

| Source Size (MB) | Index Time (Seconds)  | Index Size (MB)  | Retrieval Time (Seconds) |
|:----------------:|:---------------------:|:----------------:|:------------------------:|
|        10        |          2.9          |      2.027       |          0.001           |
|       100        |         19.1          |      16.668      |          0.002           |
|       250        |         51.1          |      41.823      |          0.003           |
|       500        |         134.1         |      80.340      |          0.003           |
|       1000       |          Nan          |       Nan        |           Nan            |

#### SPIMI (256MB of RAM)

| Source Size (MB) | Index Time (Seconds) | Compressed Index Size (MB) |  Retrieval Time (Seconds)  |
|:----------------:|:--------------------:|:--------------------------:|:--------------------------:|
|        10        |         3.1          |           0.419            |           0.007            |
|       100        |         24.4         |           2.638            |           0.010            |
|       250        |         63.1         |           6.102            |           0.011            |
|       500        |        135.4         |           11.107           |           0.013            |
|       1000       |        272.0         |           20.911           |           0.014            |

#### Compression

| Source Size (MB) | Index Size (MB) | Compressed Index Size (MB) |
|:----------------:|:---------------:|:--------------------------:|
|        10        |      0.969      |           0.419            |
|       100        |      8.727      |           2.638            |
|       250        |     22.463      |           6.102            |
|       500        |     42.900      |           11.107           |
|       1000       |     99.767      |           20.911           |

### Boolean Search

Performs boolean search queries on a given dictionary.

Supports:

- AND/OR/NOT
- () priorities
- "" phrases
- Distance base search (e.g. "\5 romeo juliet"
  means it is required to find all documents
  that have the distance (the number of words) between 'romeo' and 'juliet' less or equal to 5)

#### Examples

```shell
> romeo OR juliet
> romeo AND NOT juliet
> (romeo AND juliet) AND NOT ceasar
> "a very long phrase"
> "\5 romeo juliet" 
```

### Joker (Wildcard) Search

#### Term Permutation Index

#### K-Gram Index
