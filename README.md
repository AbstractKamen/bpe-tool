# bpe-tool

A tool which can compress text using binary pair encoding. 

# References 
- https://www.twitch.tv/tsoding inspirational programmer :D
- https://en.wikipedia.org/wiki/Byte_pair_encoding

# TODO

1. turn into proper command line tool [v]
1. build and release artifacts with maven and git actions [v]
1. finish the tool since description is basically false advertisement :D [v]
    - encode input text in a readable way after it has been compressed [v]
    - decode compressed text to produce the original [v]
1. follow zozin, he mentioned something about grammars and grammars are cool! [v]
1. improve performance [x]
   - maybe an IntObjectHashMap implementation [x]
   - multi-thread compress/decompress [x]
