#!/bin/bash
# Simple Terminal Supermarket - Test Script

echo "================================"
echo "StS - Test Script"
echo "================================"
echo ""

# Compile
echo "✓ Compiling..."
mvn clean compile -q

# Run with test input
echo "✓ Running application..."
echo ""
(
echo "LOGIN Alice"
sleep 0.2
echo "BUY 1 2"
sleep 0.2
echo "BUY 2 1"
sleep 0.2
echo "HELP"
sleep 0.2
echo "LOGS"
sleep 0.2
echo "CHECKOUT"
sleep 0.2
echo "EXIT"
) | java -cp target/classes com.sts.Main

echo ""
echo "================================"
echo " Test completed"
echo "================================"

