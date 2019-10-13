
# param 1: file name 
# param 2-N: different block sizes

import sys

file_name = sys.argv[1]
block_sizes = map(lambda block_size: int(block_size), sys.argv[2:])

print("This script executes matrix multiplication for the matrixes in file",
        "'" + file_name + "'", "using the naive strategy and the blocked",
        "strategy for block sizes:", ", ".join(sys.argv[2:]))
print("The result of this script is a graph 'graph.png' in the current",
      "directory that shows the results with matrix size on the x-axis and",
      "time on the y-axis. Create one line for each block size and one line",
      "for the naive strategy.")
