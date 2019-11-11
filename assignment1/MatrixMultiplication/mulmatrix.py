
# param 1: file name 
# param 2-N: different block sizes

import sys
import subprocess
import matplotlib.pyplot as plt

file_name = sys.argv[1]
block_sizes = list(map(lambda block_size: int(block_size), sys.argv[2:]))

print("This script executes matrix multiplication for the matrixes in file",
        "'" + file_name + "'", "using the naive strategy and the blocked",
        "strategy for block sizes:", ", ".join(sys.argv[2:]))
print("The result of this script is a graph 'graph.png' in the current",
      "directory that shows the results with matrix size on the x-axis and",
      "time on the y-axis. Create one line for each block size and one line",
      "for the naive strategy.")

methods = ["naive", "blocked"] if len(block_sizes) > 0 else ["naive"]

fd_in = open(file_name, "r")
output_list = []
line_counter = 0
for line in fd_in.readlines():
    print("Line {}: {}".format(line_counter, line))
    line_counter += 1
    arguments = line.split()
    for method in methods:
        if method == "naive":
            output_filename = "output_{}_{}_{}_{}".format(method, arguments[0], arguments[1], arguments[2])
            print("\tCreated output: {}".format(output_filename))
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
        if method == "blocked":
            for block_size in block_sizes:
                output_filename = "output_{}_{}_{}_{}_{}".format(method, arguments[0], arguments[1], arguments[2], block_size)
                print("\tCreated output: {}".format(output_filename))
                output_list.append(output_filename)
                fd_out = open(output_filename, "w+")
                subprocess.call(
                        "./matrix_multiplication {} {} {} {} {}".format(
                            method,
                            arguments[0],
                            arguments[1],
                            arguments[2],
                            block_size),
                        stdin=None,
                        stdout=fd_out,
                        stderr=fd_out,
                        shell=True)
                fd_out.close()
fd_in.close()

print("==Start creating plots==")
plot_points_naive = [[], []]
plot_points_by_block_size = {}
for output_filename in output_list:
    # Open the file
    fd_in = open(output_filename, "r")
    # Get the matrix dimension N, block size B and the time from the file
    dimension = 0
    time = 0.0
    block_size = 0
    method = "naive"
    for line in fd_in.readlines():
        if line.startswith("N="):
            dimension = int(line.split("=")[1])
        if line.startswith("time"):
            time = float(line.split()[1])
        if line.startswith("B="):
            block_size = int(line.split("=")[1])
        if line.startswith("method="):
            method = line.split("=")[1]
            # Remove newline
            method = method.rstrip()
    print("Processing {}".format(method))
    # Add point to plot list for naive
    if method == "naive":
        print("Adding point ({}, {}) for naive".format(
            dimension,
            time))
        plot_points_naive[0].append(dimension)
        plot_points_naive[1].append(time)
    elif method == "blocked":
    # Add point to plot list dictionary by block size
        print("Adding point ({}, {}) for block size {}".format(
            dimension,
            time,
            block_size))
        if block_size in plot_points_by_block_size:
            plot_points_by_block_size[block_size][0].append(dimension)
            plot_points_by_block_size[block_size][1].append(time)
        else:
            plot_points_by_block_size[block_size] = [[], []]
            plot_points_by_block_size[block_size][0].append(dimension)
            plot_points_by_block_size[block_size][1].append(time)
# Plot the list
print("naive: {}".format(plot_points_naive))
print("blocked: {}".format(plot_points_by_block_size))
plt.plot(plot_points_naive[0], plot_points_naive[1], label="naive")
for block_size in plot_points_by_block_size:
    plt.plot(
            plot_points_by_block_size[block_size][0],
            plot_points_by_block_size[block_size][1],
            label="Block size {}".format(block_size))
plt.legend()
plt.show()
