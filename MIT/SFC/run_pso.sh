#!/usr/bin/env sh

export PATH=/usr/local/share/Qt/5.9/gcc_64/bin:$PATH
qmake || qmake-qt5
LD_LIBRARY_PATH=/usr/local/share/Qt/5.9/gcc_64/lib make
QT_QPA_PLATFORM_PLUGIN_PATH=$QT_QPA_PLATFORM_PLUGIN_PATH:/usr/local/share/Qt/5.9/gcc_64/plugins/ ./PSO
