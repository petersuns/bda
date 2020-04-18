# 4 models:
# 1. nbfh:
# 2. nbcms:
# 3. pfh: learningRate, 
# 4. pcms:learningRate, 
# default  LEARNING_RATE=0.5 LOG_NB_BUCKETS=25 NB_HASHES=10 THRESHOLD=0.5 MAX_N=1

# change THRESHOLD to [0,0.25,0.5,0.75,2.0] 
(time THRESHOLD=0.0 LOG_NB_BUCKETS=25 LEARNING_RATE=0.5 NB_HASHES=10 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
(time THRESHOLD=0.25 LOG_NB_BUCKETS=25 LEARNING_RATE=0.5 NB_HASHES=10 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
(time THRESHOLD=0.5 LOG_NB_BUCKETS=25 LEARNING_RATE=0.5 NB_HASHES=10 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
(time THRESHOLD=0.75 LOG_NB_BUCKETS=25 LEARNING_RATE=0.5 NB_HASHES=10 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
(time THRESHOLD=1 LOG_NB_BUCKETS=25 LEARNING_RATE=0.5 NB_HASHES=10 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
# change LOG_NB_BUCKETS to [5,10,15,25,30]
(time LOG_NB_BUCKETS=5 LEARNING_RATE=0.5 NB_HASHES=10 THRESHOLD=0.5 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
(time LOG_NB_BUCKETS=10 LEARNING_RATE=0.5 NB_HASHES=10 THRESHOLD=0.5 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
(time LOG_NB_BUCKETS=15 LEARNING_RATE=0.5 NB_HASHES=10 THRESHOLD=0.5 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
(time LOG_NB_BUCKETS=25 LEARNING_RATE=0.5 NB_HASHES=10 THRESHOLD=0.5 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
(time LOG_NB_BUCKETS=30 LEARNING_RATE=0.5 NB_HASHES=10 THRESHOLD=0.5 MAX_N=1 make nbfh_small) &>> log_nbfh_small 
# change LEARNING_RATE to [0.0001,0.25,0.5,0.75,0.9]
(time LEARNING_RATE=0.01 NB_HASHES=10 THRESHOLD=0.5 LOG_NB_BUCKETS=25 MAX_N=1 make pfh_small) &>> log_pfh_small
(time LEARNING_RATE=0.1 NB_HASHES=10 THRESHOLD=0.5 LOG_NB_BUCKETS=25 MAX_N=1 make pfh_small) &>> log_pfh_small 
(time LEARNING_RATE=0.25 NB_HASHES=10 THRESHOLD=0.5 LOG_NB_BUCKETS=25 MAX_N=1 make pfh_small) &>> log_pfh_small 
(time LEARNING_RATE=0.5 NB_HASHES=10 THRESHOLD=0.5 LOG_NB_BUCKETS=25 MAX_N=1 make pfh_small) &>> log_pfh_small 
(time LEARNING_RATE=0.75 NB_HASHES=10 THRESHOLD=0.5 LOG_NB_BUCKETS=25 MAX_N=1 make pfh_small) &>> log_pfh_small 
(time LEARNING_RATE=0.9 NB_HASHES=10 THRESHOLD=0.5 LOG_NB_BUCKETS=25 MAX_N=1 make pfh_small) &>> log_pfh_small
 # change NB_HASHES to [1,5,10,15,20,25,30]
 (time NB_HASHES=1 MAX_N=1 LEARNING_RATE=0.5 THRESHOLD=0.5 LOG_NB_BUCKETS=25 make pcms_small) &>> log_pcms_small
 (time NB_HASHES=5 MAX_N=1 LEARNING_RATE=0.5 THRESHOLD=0.5 LOG_NB_BUCKETS=25 make pcms_small) &>> log_pcms_small 
 (time NB_HASHES=10 MAX_N=1 LEARNING_RATE=0.5 THRESHOLD=0.5 LOG_NB_BUCKETS=25 make pcms_small) &>> log_pcms_small 
 (time NB_HASHES=15 MAX_N=1 LEARNING_RATE=0.5 THRESHOLD=0.5 LOG_NB_BUCKETS=25 make pcms_small) &>> log_pcms_small 
 (time NB_HASHES=20 MAX_N=1 LEARNING_RATE=0.5 THRESHOLD=0.5 LOG_NB_BUCKETS=25 make pcms_small) &>> log_pcms_small 
 # change MAX_N to [1,2,3,4,5]
(time THRESHOLD=0.5 MAX_N=1 LEARNING_RATE=0.5 LOG_NB_BUCKETS=25 NB_HASHES=10 make nbfh_small) &>> log_nbfh_small 
(time THRESHOLD=0.5 MAX_N=2 LEARNING_RATE=0.5 LOG_NB_BUCKETS=25 NB_HASHES=10 make nbfh_small) &>> log_nbfh_small 
(time THRESHOLD=0.5 MAX_N=3 LEARNING_RATE=0.5 LOG_NB_BUCKETS=25 NB_HASHES=10 make nbfh_small) &>> log_nbfh_small 
(time THRESHOLD=0.5 MAX_N=4 LEARNING_RATE=0.5 LOG_NB_BUCKETS=25 NB_HASHES=10 make nbfh_small) &>> log_nbfh_small 
(time THRESHOLD=0.5 MAX_N=5 LEARNING_RATE=0.5 LOG_NB_BUCKETS=25 NB_HASHES=10 make nbfh_small) &>> log_nbfh_small 




