

咱们几个朋友在 GitHub 上合作写一个项目（比如 Shapeville），需要了解以下内容。

⸻

🚀 基本操作术语（先搞懂这几个就行）

⸻

✅ Push（推送）
•	人话解释：你把你电脑上的代码上传到 GitHub。
•	就像是你把自己写的作业交到公共仓库，让大家都能看到。
•	💡常见命令：git push

⸻

✅ Pull（拉取）
•	人话解释：你从 GitHub 上下载最新的项目代码到你电脑上。
•	就像是你同步别人的更新，确保你手上的是最新版。
•	💡常见命令：git pull

⸻

✅ Clone（克隆）
•	人话解释：你第一次下载整个项目，放在你本地开始工作。
•	相当于「拷贝一份全套作业」开始写。
•	💡常见命令：git clone <仓库地址>

⸻

✅ Branch（分支）
•	人话解释：你在主线之外，开了个自己的小分线来改代码。
•	比如主线是 main，你可以创建 test-login 这个分支，不影响主线。
•	工作流推荐：每个人改动用独立分支，改完再合并。

⸻

🔁 PR 和合并相关的术语（配合协作）

⸻

✅ PR（Pull Request）拉取请求
•	人话解释：你写好了一个功能（在你的分支上），申请把你的代码合并到主分支。
•	就像你写完作业，请别人帮你看看，觉得没问题再贴到正稿上。
•	PR 是团队协作的核心流程。

⸻

✅ Review（代码评审）
•	人话解释：别人点开你的 PR，看你的代码，提建议，点「通过」。
•	就像组员帮你查错、提修改意见。

⸻

✅ Merge（合并）
•	人话解释：PR 审核通过后，你的改动会被合并进主分支。
•	相当于你写的部分通过检查，被贴进正式稿里了。

⸻

✅ 提交（Commit）是什么意思？
提交是你在本地代码仓库中记录一次修改的行为，相当于「保存一次阶段性进度」并附带说明。
•	每次 commit 包括两部分：
1.	你改的内容（比如你改了哪个文件，增删了哪些代码）；
2.	一个说明这次改动的备注（commit message），比如：Fix login bug。
•	💡 就像写作业每改完一页你都拍个照+写张便签：“这一页我把错题改了”。

⸻
我已经做好的：
•	建好了仓库 + 主分支 main 启用了分支保护规则
•	你是 Admin，其余成员是 Write 权限

我之后需要做的
1. Review PR
•	查看 PR → 可进行代码审查、评论、请求修改
•	如无问题 → Approve ✅
2. 合并 PR
•	你点击 Merge pull request 完成合并到 main


✅ 我能在 IDEA 上进行 review 吗？

不能直接进行 PR review，但你可以辅助查看代码。

JetBrains IDEA（IntelliJ IDEA）本身 不支持 GitHub 的代码审查功能（Review），因为 PR review 是发生在 GitHub 网页上的行为。

不过，你可以：
•	在 IDEA 中查看 PR 内容、diff、切换分支（需要安装 GitHub 插件）；
•	实际评审和点通过/驳回，需要在 GitHub 网站上操作。

⸻

✅ 我在哪里看谁可以 Review？
1.	打开 GitHub 仓库的 Pull Requests 页（顶部菜单点 “Pull requests”）。
2.	点进某个 PR，你会看到右侧有 Reviewers：
•	GitHub 会显示：
•	谁是推荐 reviewer（比如对应代码的 code owner）；
•	谁已经被你或系统指派去审查；
•	谁已经给出 Review 意见（Approved、Request changes、Commented）。

✅ 从仓库角度查看权限
1.	打开仓库主页（如 Shapeville 仓库）。
2.	点击顶部菜单的 Settings → 左侧选择 Collaborators and teams。
3.	在 “Teams” 和 “People” 区域，可以看到哪些人/团队有访问权，以及他们对应的角色权限。

✅ 我设定的5人权限模式：成员角色
GitHub 权限等级
原因/能力说明
你（组织拥有者）
Admin
管理仓库设置、权限分配、分支保护规则等
其他4人（协作者）
Write
可以推送代码、创建分支、发起 PR，参与开发




✅ 我设定的流程：

【开发者】创建新分支（如 feature/task1）
↓
【提交代码】写好后推送到 feature/task1 分支
↓
【打开 PR】请求合并到 main 分支
↓
【我review】通过，允许合并
↓
【成功合并】进入 main 分支





协作者要做的：
🧑‍💻 协作者（写权限成员）操作流程：
1.	克隆仓库
•	在 GitHub 上复制仓库地址
•	用 IDEA 打开 → VCS > Get from Version Control → 粘贴地址 → 克隆到本地
2.	创建新分支（在 IDEA UI）
•	推荐命名方式如 feature/任务名 或 fix/bug描述
•	在 IDEA 右下角 Git 分支切换器新建并切换分支
3.	进行代码修改、提交
•	改动完成后，在 IDEA 中 Commit 并 Push 到远程（当前分支）
4.	推送前先拉取主分支防冲突（可选但推荐）
•	切回 main 分支 → 拉取最新代码 → 切回你自己的分支 → rebase 或 merge 一下
•	保证不会引发冲突
5.	推送分支到远程仓库
•	右上角 Push → 远程会出现新分支
6.	在 GitHub 上发起 Pull Request
•	GitHub 会提示你刚推的新分支
•	点 Compare & Pull Request 发起合并请求
•	说明修改内容、选择合并到的目标分支（通常是 main）