<!--
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-10 16:19:13
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2023-01-31 16:58:45
 * @Description:  
-->
# concurrent_ticketing_system
Reading [report.md](report.md) For more details.

## build
```
cd <project_root_path>
cd myproject
sh test.sh                  # rebuild and generate history
sh temp_very.sh             # verify the history
sh verilin.sh               # verify linearizable
sh benchmark.sh             # test the performance
```


## Linearizability Checking
Has passed `verilin.sh`.
```
route: 3, coach: 3, seatnum: 5, station: 5, refundRatio: 10, buyRatio: 30, inquiryRatio: 60
history size = 3999, region size = 3999, max_region_size = 1
Verification Finished.
40ms
VeriLin
```

## Benchmark
Has updated performance testing code in `Test.java`, the result is stored in the file named `result.csv`.