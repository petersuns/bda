
# param 1: file name 
# param 2-N: different block sizes

import sys
import subprocess
import matplotlib.pyplot as plt

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
output_list = []
output_counter = 0
for line in fd_in.readlines():
    print("Line: {} {}".format(output_counter, line))
    arguments = line.split()
    output_filename = "output_{}_{}_{}_{}".format(method, arguments[0], arguments[1], arguments[2])
    output_list.append(output_filename)
    fd_out = open(output_filename, "w+")
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
    output_counter += 1
fd_in.close()

plot_points = [[], []]
for output_filename in output_list:
    # Open the file
    fd_in = open(output_filename, "r")
    # Get the matrix dimension N and the time from the file
    dimension = 0
    time = 0.0
    for line in fd_in.readlines():
        if line.startswith("N="):
            dimension = int(line.split("=")[1])
        if line.startswith("time"):
            time = float(line.split()[1])
    # Add point to plot list
    print("Adding point ({}, {})".format(dimension, time))
    plot_points[0].append(dimension)
    plot_points[1].append(time)
# Plot the list
plt.plot(plot_points[0], plot_points[1])
plt.show()
