#!/bin/bash
ant compile && java -cp "./build/classes/;./lib/gson-2.6.2.jar" ChatBot.Main
