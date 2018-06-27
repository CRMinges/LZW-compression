# LZW-compression
A simple implementation of the Lempel-Ziv-Welch compression algorithm.

# About
LZW (Lempel-Ziv-Welch) compression is one of the earlier compression algorithms, created in 1978! 
It mainly became popular for compressing the GIF image format. The main idea behind the algorithm
is that it looks for repeated patterns of data (character sequence, bit sequences, etc.), and
replaces the pattern with a code (in this case, a value between 0 and 255). A dictionary holds
the mapping between a data sequence and a corresponding code, so when a pattern is seen later in
the data, we can check to see if it has been encountered already, and if so, replace it with the 
corresponding code from the dictionary. In the case of this implementation, the assumption is that
we will find character sequences longer than 8-bits that we can replace with an 8-bit code. 

# Usage
When running the program from the command line, you will first be prompted to delare whether you
wish to compress or decompress a file. When compressing, you will first give the path
(including file name) of file you wish to compress. Then you give the name you want to give to the
compressed file (this will be located in same directory as file you are compressing). When 
decompressing, give full path of file compressed file, followed by the name (and possibly path) of 
the decompressed file. 

# References
www.eecis.udel.edu/~amer/CISC651/lzw.and.gif.explained.html

www.cs.columbia.edu/~allen/S14/NOTES/lzw.pdf

web.mit.edu/6.02/www/s2012/handouts/3.pdf
