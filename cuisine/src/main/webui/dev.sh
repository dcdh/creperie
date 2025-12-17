#!/bin/bash
find src -type f | grep -v '^src/api/' | entr -r sh -c 'npm run dev'
