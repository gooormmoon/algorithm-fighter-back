-- insert member
-- INSERT INTO member(name, nickname, login_id, password, role, profile_image_url, description, alert)
-- VALUES ('testname', 'immm', 'test', 'test!', '역할', 'https://avatars.githubusercontent.com/u/62759872?s=400&v=4', '게임 고수', '알람1');

-- INSERT INTO member(name, nickname, login_id, password, role, profile_image_url, description, alert)
-- VALUES ('testname2', 'immm2', 'test2', 'test!', '역할', 'https://avatars.githubusercontent.com/u/62759872?s=400&v=4', '게임 하수', '알람1');

-- insert filestorage
-- INSERT INTO filestorage(name, member_id) VALUES ('fileStorage1', '1');

-- insert file
-- INSERT INTO file(content, name, type, path, language, file_storage_id, parent_id) VALUES ('import java.util.*;
--
--     class Group{
--         int totalCandy,cnt;
--
--         public Group(int w, int cnt){
--             this.totalCandy =w;
--             this.cnt= cnt;
--         }
--     }
--
--     public class Main {
--         static int n,m,k;
--         static int[] candy;
--         static int totalCandy,cnt;
--         static List<ArrayList<Integer>> friends;
--         static ArrayList<Group> groups;
--         static boolean[] check;
--
--         public static void main(String args[]) {
--             Scanner sc = new Scanner(System.in);
--
--             n = sc.nextInt();
--             m = sc.nextInt();
--             k = sc.nextInt();
--             friends = new ArrayList<ArrayList<Integer>>();
--
--             for(int i=0; i<=n; i++) friends.add(new ArrayList<>());
--
--             candy= new int[n+1];
--             check= new boolean[n+1];
--
--             for(int i=1; i<=n; i++){
--                 candy[i] = sc.nextInt();
--             }
--
--             for(int i=0; i<m; i++){
--                 int a = sc.nextInt();
--                 int b = sc.nextInt();
--
--                 friends.get(a).add(b);
--                 friends.get(b).add(a);
--             }
--
--             DivideGroup();
--             getCandy();
--         }
--
--
--         private static void DivideGroup() {
--             groups = new ArrayList<>();
--             for(int i=1; i<=n; i++){
--                 if(!check[i]){
--                     totalCandy = 0;
--                     cnt= 0;
--                     DFS(i);
--                     groups.add(new Group(totalCandy,cnt));
--                 }
--             }
--         }
--
--         private static void DFS(int v) {
--             check[v] = true;
--             totalCandy += candy[v];
--             cnt += 1;
--             for(int nv : friends.get(v)){
--                 if(check[nv]){
--                     continue;
--                 }
--                 DFS(nv);
--             }
--         }
--
--         private static void getCandy() {
--             int dp[][] = new int[2][k+1];
--
--             for (Group group : groups) {
--                 for(int i=0; i<=k; i++)', '할로윈의양아치.java', 'file', '/storage', 'java', 1, NULL);

-- insert algorithmproblem
INSERT INTO algorithmproblem(title, level ,content, recommend_time)
VALUES
    ('문자 찾기','1','한 개의 문자열을 입력받고,특정 문자를 입력받아 해당 특정문자가 입력받은 문자열에 몇 개 존재하는지 알아내는 프로그램을 작성하세요. 대소문자를 구분하지 않습니다.문자열의 길이는 100을 넘지 않습니다.',10),
    ('대소문자 변환','1','대문자와 소문자가 같이 존재하는 문자열을 입력받아 대문자는 소문자로 소문자는 대문자로 변환하여 출력하는 프로그램을 작성하세요.',10),
    ('문장 속 단어','1','한 개의 문장이 주어지면 그 문장 속에서 가장 긴 단어를 출력하는 프로그램을 작성하세요. 문장속의 각 단어는 공백으로 구분됩니다.',10),
    ('단어 뒤집기','1','N개의 단어가 주어지면 각 단어를 뒤집어 출력하는 프로그램을 작성하세요',10),
    ('특정 문자 뒤집기','2','영어 알파벳과 특수문자로 구성된 문자열이 주어지면 영어 알파벡만 뒤집고, 특수문자는 자기 자리에 그대로 있는 문자열을 만들어 출력하는 프로그램을 작성하세요.',20),
    ('중복문자제거','1','소문자로 된 한개의 문자열이 입력되면 중복된 문자를 제거하고 출력하는 프로그램을 작성하세요. 중복이 제거된 문자열의 각 문자는 원래 문자열의 순서를 유지합니다.',15),
    ('회문 문자열','2','앞에서 읽을 때나 뒤에서 읽을 때나 같은 문자열을 회문 문자열이라고 합니다. 문자열이 입력되면 해당 문자열이 회문 문자열이면 \"YES\", 회문 문자열이 아니면 \"NO\"를 출력하는 프로그램을 작성하세요. 단 회문을 검사할 때 대소문자를 구분하지 않습니다.',15),
    ('유요한 팰린드롬','2','앞에서 읽을 때나 뒤에서 읽을 때나 같은 문자열을 팰린드롬이라고 합니다. 문자열이 입력되면 해당 문자열이 팰린드롬이면 \"YES\", 아니면 \"NO\"를 출력하는 프로그램을 작성하세요.',25),
    ('숫자만 추출','1','문자와 숫자가 섞여있는 문자열이 주어지면 그 중 숫자만 추출하여 그 순서대로 자연수를 만듭니다. 만약 \"tge0a1h205er\"에서 숫자만 추출하면 0,1,2,0,5이고 이것을 자연수를 만들면 1205이 됩니다. 추출하여 만들어지는 자연수는 100,000,000을 넘지 않습니다.',20),
    ('가장 짧은 문자거리','3','한 개의 문자열 s와 문자 t가 주어지면 문자열 s의 각 문자가 문자 t와 떨어진 최소거리를 출력하는 프로그램을 작성하세요.',25),
    ('문자열 압축','1','알파벳 대문자로 이루어진 문자열을 입력받아 같은 문자가 연속으로 반복되는 경우 반복되는 문자 바로 오른쪽에 반복 횟수를 표기하는 방법으로 문자열을 압축하는 프로그램을 작성하시오. 단 반복횟수가 1인 경우 생략합니다.',15),
    ('암호','2','현수는 영희에게 알파벳 대문자로 구성된 비밀편지를 매일 컴퓨터를 이용해 보냅니다. 비밀편지는 현수와 영희가 서로 약속한 암호로 구성되어 있습니다. 비밀편지는 알파벳 한 문자마나 # 또는 *이 일곱 개로 구성되어 있습니다. 만약 현수가 \"#*****#\"으로 구성된 문자를 보냈다면 영희는 현수와 약속한 규칙대로 다음과 같이 해석합니다. 1.\"#*****#\"를 일곱자리의 이진수로 바꿉니다. #은 이진수의 1로, *은 이진수의 0으로 변환합니다. 결과는 \"1000001\"로 변환됩니다. 2.바뀐 2진수를 10진수화 합니다. \"1000001\"을 10진수화 하면 65가 됩니다. 3.아스키 번호가 65문자로 변환합니다. 즉 아스키번호 65는 대문자 \"A\"입니다. 참고로 대문자들의 아스키 번호는 \"A\"는 65번~ \"Z\"는 90번입니다. 현수가 보낸 신호를 해석해주는 프로그램을 작성하세요.',25),
    ('최대점수 구하기(냅색 알고리즘)','5','이번 정보올림피아드대회에서 좋은 성적을 내기 위하여 현수는 선생님이 주신 N개의 문제를 풀려고 합니다. 각 문제는 그것을 풀었을 때 얻는 점수와 푸는데 걸리는 시간이 주어지게 됩니다. 제한시간 M안에 N개의 문제 중 최대점수를 얻을 수 있도록 해야 합니다.(해당문제는 해당시간이 걸리면 푸는 걸로 간주한다. 한 유형당 한개만 풀 수 있습니다.)',50);

-- insert chatroom
INSERT INTO chatroom(chatroom_name) VALUES('GLOBAL');

-- insert message
-- INSERT INTO message(content, sender_id, chatroom_id) VALUES ('안녕하세요!!~!', 1,1);

-- insert member_chatroom
-- INSERT INTO member_chatroom(chatroom_id, member_id) VALUES (1,1);

-- insert gameresult
-- INSERT INTO gameresult(running_time, member_code_content, other_member_code_content, algorithm_problem_id, chatroom_id)
-- VALUES('20:30', 'code1' , 'code2', 1 , 1);

-- insert member_gameresult
-- INSERT INTO member_gameresult(game_result_id, member_id) VALUES (1, 1);

-- insert testcase
INSERT INTO testcase(test_input, test_output, algorithm_problem_id)
VALUES
    ('computerprogramming r','3',1),
    ('Computercooler c','2',1),
    ('sssssss','7',1),
    ('itistimetogoii','i',1),
    ('tttccttccTT','T',1),
    ('StuDY','sTUdy',2),
    ('akfHHLkdjlgKHL','AKFhhlKDJLGkhl',2),
    ('znlakHLnvLHHLgkdieytYOY','ZNLAKhlNVlhhlGKDIEYTyoy',2),
    ('dkieoYOIUOotokdjgljajYOYOHKGhkgLKJLKJgBlkBlLJ','DKIEOyoiuoOTOKDJGLJAJyoyohkgHKGlkjlkjGbLKbLlj',2),
    ('djkjgKLUOIHkjHGYhUYFUYGjGUYGUYfUFUGUguYURrUHGUYUguOK','DJKJGkluoihKJhgyHuyfuygJguyguyFufuguGUyurRuhguyuGUok',2),
    ('it is time to study','study',3),
    ('dkjg LKKL KJkjglkd Kjgkd LKKJLJLJLKJLLLLLLL','LKKJLJLJLKJLLLLLLL',3),
    ('eitoiw iruow witouweiotwiowioei eiiuow e','witouweiotwiowioei',3),
    ('jddgshsgskjkdj dijglolkdwoig dkjkljglkd gksjlkjgls gkjldkjgla','jddgshsgskjkdj',3),
    ('loveispower','loveispower',3),
    ('3 good Time Big','doog emiT giB',4),
    ('5 dkjg Gkkjdg DGSGjkjdgkAGGA dkKJKLHLkjlkd HLKJLKJLKHL','gjkd gdjkkG AGGAkgdjkjGSGD dkljkLHLKJKkd LHKLJKLJKLH',4),
    ('7 dkjg Gkkjdg DGSGjkjdgkAGGA dkKJKLHLkjlkd HLKJLKJLKHL dkjkghlsKJLJLJLKJK dkNBHGKJHKJHKJHKJHKJHKJHKJH','gjkd gdjkkG AGGAkgdjkjGSGD dkljkLHLKJKkd LHKLJKLJKLH KJKLJLJLJKslhgkjkd HJKHJKHJKHJKHJKHJKHJKGHBNkd',4),
    ('10 dkjg Gkkjdg DGSGjkjdgkAGGA dkKJKLHLkjlkd HLKJLKJLKHL dkjkghlsKJLJLJLKJK dkNBHGKJHKJHKJHKJHKJHKJHKJH djklgj skjglkjkljdlk kdjglk','gjkd gdjkkG AGGAkgdjkjGSGD dkljkLHLKJKkd LHKLJKLJKLH KJKLJLJLJKslhgkjkd HJKHJKHJKHJKHJKHJKHJKGHBNkd jglkjd kldjlkjklgjks klgjdk',4),
    ('20 dkjg Gkkjdg DGSGjkjdgkAGGA dkKJKLHLkjlkd HLKJLKJLKHL dkjkghlsKJLJLJLKJK dkNBHGKJHKJHKJHKJHKJHKJHKJH djklgj skjglkjkljdlk kdjglk dkjgdg Gkkjdgsgdg DGSGjkjdgkAGGAdgs dkKJKLHLkjlkdsg HLKJLKJLKHLsg dkjkghlsKJLJLJLKJKsgs dkNBHGKJHKJHKJHKJHKJHKJHKJHsg djklgjsg skjglkjkljdlksh kdjglksbgs','gjkd gdjkkG AGGAkgdjkjGSGD dkljkLHLKJKkd LHKLJKLJKLH KJKLJLJLJKslhgkjkd HJKHJKHJKHJKHJKHJKHJKGHBNkd jglkjd kldjlkjklgjks klgjdk gdgjkd gdgsgdjkkG sgdAGGAkgdjkjGSGD gsdkljkLHLKJKkd gsLHKLJKLJKLH sgsKJKLJLJLJKslhgkjkd gsHJKHJKHJKHJKHJKHJKHJKGHBNkd gsjglkjd hskldjlkjklgjks sgbsklgjdk',4),
    ('a#b!GE*T@S','S#T!EG*b@a',5),
    ('kdj#@kdjg%$#kdjgk@kd$dk','kdd#@kkgj%$#dkgjd@kj$dk',5),
    ('kqQdj#@kd#g%$#kdj&&gk@kd$dQGk','kGQdd#@kk#g%$#jdk&&gd@kj$dQqk',5),
    ('kqQ!DGSGSdj#@kd#g%$#kdj&&gk@kd$d#%&DGS@!DHSQGk','kGQ!SHDSGDd#@dk#k%$#gjd&&kg@dk$j#%&dSG@!SGDQqk',5),
    ('kHSHHS#qQ!DGSG#@Sdj#@kd#g%$#kdj&&gk@kd$d#%&DGS@!DH%SQGk#','kGQSHD#SG!Dddk#@kgj#@dk#g%$#dkj&&dS@GS$G#%&DQq@!SH%HSHk#',5),
    ('ksekkset','kset',6),
    ('kjkgjlskjekieogiwo','kjglseiow',6),
    ('kdkgksjgkjlsjgkjsljgkjaksjg','kdgsjla',6),
    ('eiotuoiwtitoiywiotieoiutoiwioweuotiuwoieut','eiotuwy',6),
    ('qiutoiwuotiqpituoiwuiotuowutowiutoityioqp','qiutowpy',6),
    ('gooG','YES',7),
    ('tttttttttttttt','YES',7),
    ('sssssssssssssssksssssssssssssss','YES',7),
    ('kstudkgksjlkgjlksjdggkkllllllllllllllllllllllsjgksjldgjlllllllllllllllgjks','NO',7),
    ('skSKskuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuKSksks','YES',7),
    ('Tae,aba;e#%a*T','YES',8),
    ('found7, timk: study; Yduts; emit, 7Dnuof','NO',8),
    ('found7, time: study; Yduts; emit, 7Dnuof','YES',8),
    ('kdjg$@kjkldjkg%@dkjgkj','NO',8),
    ('a sd fg #%hjkl; %#$@! lkj&*hgfd s ##a','YES',8),
    ('kdk1k0kdjfkj0kkdjkfj0fkd','1000',9),
    ('dkf0jkk0dkjkgjljl1kgh0ekjlkjf2lkjsklfjlkdj','102',9),
    ('Akdj0Gk1djADG2SDGkdjf','12',9),
    ('Akdj0Gk1djADG2SDGkdj0f','120',9),
    ('Akdj0Gk1dgdgdAGSGAG3DGGA45GAGADGDGdjADG2SDGkdj0f','134520',9),
    ('teachermode e','1 0 1 2 1 0 1 2 2 1 0',10),
    ('fkdgkjdflkgjljslgjkfldjlkfdg f','0 1 2 3 3 2 1 0 1 2 3 4 5 6 5 4 3 2 1 0 1 2 3 2 1 0 1 2',10),
    ('kkkkkkkk k','0 0 0 0 0 0 0 0',10),
    ('eochjgoekghlakegh h','3 2 1 0 1 2 3 3 2 1 0 1 2 3 2 1 0',10),
    ('timeout t','30 1 2 3 2 1 0',10),
    ('KKTFFFFFFEEE','K2TF6E3',11),
    ('KSTTTSEEKFKKKDJJGG','KST3SE2KFK3DJ2G2',11),
    ('KDKGKKSKKFJKKKKSLSSSSKFKSSSS','KDKGK2SK2FJK4SLS4KFKS4',11),
    ('KKKKKDDDDDKDDDKKSKKFJKKKKSLSSSSKFKSSSS','K5D5KD3K2SK2FJK4SLS4KFKS4',11),
    ('KKKKTTTUUUKDDDDDKDDDKKSKKFJKYYYKUYY','K4T3U3KD5KD3K2SK2FJKY3KUY2',11),
    ('4 #****###**#####**#####**##**','COOL',12),
    ('8 #**#**##*#*#**#**#**##*#**###*#*#**#**#**##**##*##***#*#','ITISTIME',12),
    ('7 #*#*#**#**#**##**##*##***#*##**#####*#*#*##*#*#**','TIMEOUT',12),
    ('7 #*#**###*#*#**#*#*#*##***#**#***#*##**###*#*#*#**','STUDENT',12),
    ('10 #*#*#**#***#*##*****##****###**#***#***#*##*#**#*#****#*#*****##***###','TEACHERBAG',12),
    ('5 20 10 5 25 12 15 8 6 3 7 4','41',13),
    ('9 50 12 7 16 8 20 10 30 15 10 5 25 12 15 8 6 3 7 4','101',13),
    ('12 70 5 2 11 5 12 7 16 8 20 10 30 15 10 5 25 12 15 8 6 3 7 4 3 2','141',13),
    ('15 150 5 2 11 5 12 7 16 8 20 10 30 15 10 5 25 12 15 8 6 3 7 4 3 2 8 5 9 12 19 11','196',13),
    ('19 150 16 11 20 16 11 6 5 2 11 5 12 7 16 8 20 10 30 15 10 5 25 12 15 8 6 3 7 4 3 2 8 5 9 12 19 11 9 6','252',13);