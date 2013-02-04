#!/bin/bash

while true
do
    echo -ne 'DO hallo\nkey: value\n\n' | nc cloud2.ibr.cs.tu-bs.de 8037 &
    nc cloud2.ibr.cs.tu-bs.de 8037 &
    echo -ne 'WRONG Message\n\n' | nc cloud2.ibr.cs.tu-bs.de 8037 &
    echo -ne 'DO hello\nKey: Wrong^&&^Value\n\n' | nc cloud2.ibr.cs.tu-bs.de 8037 &

done
