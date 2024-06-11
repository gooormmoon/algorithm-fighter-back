-- insert member
INSERT INTO member(name, nickname, login_id, password, role, profile_image_url, description, alert)
VALUES ('testname', 'immm', 'test', 'test!', '역할', 'https://avatars.githubusercontent.com/u/62759872?s=400&v=4', '게임 고수', '알람1');

INSERT INTO member(name, nickname, login_id, password, role, profile_image_url, description, alert)
VALUES ('testname2', 'immm2', 'test2', 'test!', '역할', 'https://avatars.githubusercontent.com/u/62759872?s=400&v=4', '게임 하수', '알람1');

-- insert filestorage
INSERT INTO filestorage(name, member_id) VALUES ('fileStorage1', '1');

-- insert file
INSERT INTO file(content, name, type, path, language, file_storage_id, parent_id) VALUES ('import java.util.*;

    class Group{
        int totalCandy,cnt;

        public Group(int w, int cnt){
            this.totalCandy =w;
            this.cnt= cnt;
        }
    }

    public class Main {
        static int n,m,k;
        static int[] candy;
        static int totalCandy,cnt;
        static List<ArrayList<Integer>> friends;
        static ArrayList<Group> groups;
        static boolean[] check;

        public static void main(String args[]) {
            Scanner sc = new Scanner(System.in);

            n = sc.nextInt();
            m = sc.nextInt();
            k = sc.nextInt();
            friends = new ArrayList<ArrayList<Integer>>();

            for(int i=0; i<=n; i++) friends.add(new ArrayList<>());

            candy= new int[n+1];
            check= new boolean[n+1];

            for(int i=1; i<=n; i++){
                candy[i] = sc.nextInt();
            }

            for(int i=0; i<m; i++){
                int a = sc.nextInt();
                int b = sc.nextInt();

                friends.get(a).add(b);
                friends.get(b).add(a);
            }

            DivideGroup();
            getCandy();
        }


        private static void DivideGroup() {
            groups = new ArrayList<>();
            for(int i=1; i<=n; i++){
                if(!check[i]){
                    totalCandy = 0;
                    cnt= 0;
                    DFS(i);
                    groups.add(new Group(totalCandy,cnt));
                }
            }
        }

        private static void DFS(int v) {
            check[v] = true;
            totalCandy += candy[v];
            cnt += 1;
            for(int nv : friends.get(v)){
                if(check[nv]){
                    continue;
                }
                DFS(nv);
            }
        }

        private static void getCandy() {
            int dp[][] = new int[2][k+1];

            for (Group group : groups) {
                for(int i=0; i<=k; i++)', '할로윈의양아치.java', 'file', '/storage', 'java', 1, NULL);

-- insert algorithmproblem
INSERT INTO algorithmproblem(title, level ,content, recommend_time) VALUES ('할로윈의 양아치','3','### 문제
    ***Trick or Treat!!***

    10월 31일 할로윈의 밤에는 거리의 여기저기서 아이들이 친구들과 모여 사탕을 받기 위해 돌아다닌다. 올해 할로윈에도 어김없이 많은 아이가 할로윈을 즐겼지만 단 한 사람, 일찍부터 잠에 빠진 **스브러스**는 할로윈 밤을 즐길 수가 없었다. 뒤늦게 일어나 사탕을 얻기 위해 혼자 돌아다녀 보지만 이미 사탕은 바닥나 하나도 얻을 수 없었다.

    단단히 화가 난 **스브러스**는 거리를 돌아다니며 다른 아이들의 사탕을 빼앗기로 마음을 먹는다. 다른 아이들보다 몸집이 큰 **스브러스**에게 사탕을 빼앗는 건 어렵지 않다. 또한, **스브러스**는 매우 공평한 사람이기 때문에 한 아이의 사탕을 뺏으면 그 아이 친구들의 사탕도 모조리 뺏어버린다. (친구의 친구는 친구다?!)

    사탕을 다 빼앗긴 아이들은 거리에 주저앉아 울고 K명 이상의 아이들이 울기 시작하면 울음소리가 공명하여 온 집의 어른들이 거리로 나온다. **스브러스**가 어른들에게 들키지 않고 최대로 뺏을 수 있는 사탕의 양을 구하여라.

    **스브러스**는 혼자 모든 집을 돌아다녔기 때문에 다른 아이들이 받은 사탕의 양을 모두 알고 있다. 또한, 모든 아이는 **스브러스**를 피해 갈 수 없다.

    ### 입력

    첫째 줄에 정수 N, M, K가 주어진다. N은 거리에 있는 아이들의 수, M은 아이들의 친구 관계 수, K는 울음소리가 공명하기 위한 최소 아이의 수이다. (1≤N≤30 000, 0≤M≤100 000, 1≤K≤min{N,3 000})

    둘째 줄에는 아이들이 받은 사탕의 수를 나타내는 정수 C1,C2,⋯,Cn 이 주어진다. (1≤Ci≤10 000)

    셋째 줄부터 M개 줄에 갈쳐 각각의 줄에 정수 a, b가 주어진다. 이는 a와 b가 친구임을 의미한다. 같은 친구 관계가 두 번 주어지는 경우는 없다. (1≤a,b≤N, a≠b)

    ### 출력

    스브러스가 어른들에게 들키지 않고 아이들로부터 뺏을 수 있는 최대 사탕의 수를 출력한다.

    ### 예시

    |예제 입력1|예제 출력1|
    |---|---|
    |10 6 6<br>9 15 4 4 1 5 19 14 20 5<br>1 3<br>2 5<br>4 9<br>6 2<br>7 8<br>6 10|57|

    |예제 입력1|예제 출력1|
    |---|---|
    |5 4 4<br>9 9 9 9 9<br>1 2<br>2 3<br>3 4<br>4 5|0|', '40');

-- insert chatroom
INSERT INTO chatroom(chatroom_name) VALUES('global');

-- insert message
INSERT INTO message(content, sender_id, chatroom_id) VALUES ('안녕하세요!!~!', 1,1);

-- insert member_chatroom
INSERT INTO member_chatroom(chatroom_id, member_id) VALUES (1,1);

-- insert gameresult
INSERT INTO gameresult(running_time, member_code_content, other_member_code_content, algorithm_problem_id, chatroom_id)
VALUES('20:30', 'code1' , 'code2', 1 , 1);

-- insert member_gameresult
INSERT INTO member_gameresult(game_result_id, member_id) VALUES (1, 1);

-- insert testcase
INSERT INTO testcase(test_input, test_output, algorithm_problem_id)
VALUES ('10 6 6
    9 15 4 4 1 5 19 14 20 5
    1 3
    2 5
    4 9
    6 2
    7 8
    6 10', '57', 1),
       ('5 4 4
    9 9 9 9 9
    1 2
    2 3
    3 4
    4 5', '0', 1);