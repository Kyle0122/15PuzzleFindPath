#!/bin/bash

# Compile the Kotlin code
kotlinc main.kt SolverAStar.kt HashSetL.kt PriorityQueueS.kt FifteenPuzzle.kt -include-runtime -d mainkt.jar

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful"

    # Input folder
    input_folder="inputs/"

    # Range of input file numbers
    file_numbers=(11 43 59)

    # Loop through each input file
    for number in "${file_numbers[@]}"
    do
        input_file="${number}.txt"
        full_input_path="${input_folder}${input_file}"
        echo "Testing with input file: $full_input_path"

        # Execute the Kotlin program
        java -Xms8g -Xmx16g -XX:+AlwaysPreTouch -XX:+UseParallelGC -jar mainkt.jar "$full_input_path" "${input_folder}target_zero_last"

        # Check the exit status of the Java program
        if [ $? -eq 0 ]; then
            echo "Execution successful"
        else
            echo "Error during execution"
        fi
    done

else
    echo "Compilation failed. Please fix errors before testing."
fi
