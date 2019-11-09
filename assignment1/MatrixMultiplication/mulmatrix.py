
# param 1: file name 
# param 2-N: different block sizes

import sys
import subprocess

file_name = sys.argv[1]
block_sizes = map(lambda block_size: int(block_size), sys.argv[2:])

print("This script executes matrix multiplication for the matrixes in file",
        "'" + file_name + "'", "using the naive strategy and the blocked",
        "strategy for block sizes:", ", ".join(sys.argv[2:]))
print("The result of this script is a graph 'graph.png' in the current",
      "directory that shows the results with matrix size on the x-axis and",
      "time on the y-axis. Create one line for each block size and one line",
      "for the naive strategy.")

method = "naive"

fd_in = open(file_name, "r")
for line in fd_in.readlines():
    print("Line: {}".format(line))
    arguments = line.split()
    fd_out = open("output_{}_{}_{}_{}".format(method, arguments[0], arguments[1], arguments[2]), "w+")
    subprocess.call(
        "./matrix_multiplication {} {} {} {}".format(
            method,
            arguments[0],
            arguments[1],
            arguments[2]),
        stdin=None,
        stdout=fd_out,
        stderr=fd_out,
        shell=True)
    fd_out.close()
fd_in.close()
