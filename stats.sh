#!/usr/bin/env bash
echo 0 `grep level\":0 db/clients/[0-9]*.db | wc -l`
echo 1 `grep level\":1 db/clients/[0-9]*.db | wc -l`
echo 2 `grep level\":2 db/clients/[0-9]*.db | wc -l`
echo 3 `grep level\":3 db/clients/[0-9]*.db | wc -l`
echo 4 `grep level\":4 db/clients/[0-9]*.db | wc -l`
echo 5 `grep level\":5 db/clients/[0-9]*.db | wc -l`
echo 6 `grep level\":6 db/clients/[0-9]*.db | wc -l`
echo 7 `grep level\":7 db/clients/[0-9]*.db | wc -l`
echo 8 `grep level\":8 db/clients/[0-9]*.db | wc -l`
