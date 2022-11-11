<!--
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-10 16:19:13
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2022-11-11 17:19:11
 * @Description:  
-->
# concurrent_ticketing_system
UCAS concurrent final work.

## build
```
cd <project_root_path>
cd myproject
sh test.sh                  # rebuild and generate history
sh temp_very.sh             # verify the history
```


## result
Has passed `verilin.sh`, just use `synchroized` lock. TODO.
```
route: 3, coach: 3, seatnum: 5, station: 5, refundRatio: 10, buyRatio: 30, inquiryRatio: 60
history size = 3999, region size = 3999, max_region_size = 1
Verification Finished.
40ms
VeriLin
```
