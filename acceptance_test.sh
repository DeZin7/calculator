#!/bin/bash
test $(curl http://192.168.1.74:8765/sum\?a\=1\&b\=2) -eq 3