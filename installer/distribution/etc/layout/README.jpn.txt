Meldware Communication Suite インストール説明書

=== ようこそ ===

おめでとうございます。あなたはMeldware Communication Suiteのダウンロードに成功し、グラフィカルなインストー
ルをしています。
このインストーラはJBoss Application Serverの全く新規のインスタンスをインストールします。JBoss Application
ServerはこのMeldware Communication Suiteのバージョンに依存します。

同梱して配布されたJBoss Application Serverの設定は、defaultデプロイメントです。
インストール後、あなたの組織のセキュリティポリシーとインフラストラクチャに則りこのアプリケーションサーバを
セキュアにしてください。基本的な案内はここにあります:
http://wiki.jboss.org/wiki/Wiki.jsp?page=SecureJBoss
メールサーバとして厳密に必要とされていないより多くのサービスがこのリリースに含まれています。JBossの機能を
削るための情報については、この案内を参照してください:
http://wiki.jboss.org/wiki/Wiki.jsp?page=JBossASTuningSliming.

=== 新規のJBoss Application Serverと共にインストール ===

このリリースはJBossAS 4.0.4GAを含みます。単にインストール先のディレクトリを選択すると
JBoss Application Serverが含まれ、インストールが続行します。

=== みなさん ===

このインストール処理は基本的なMeldware Communication Suiteの設定を行います。しかし、このインストールはMCSが
サポートするすべての設定を行えません。インストール後の設定についてはここを参照してください:
http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#Configuring_Meldware
さらに私たちは開発者メーリングリスト:
http://www.buni.org/mediawiki/index.php/Mail_Lists
はもちろん、Meldware Communication Suiteユーザフォーラム:
http://buni.org/forums/
を通して積極的にフィードバックを受け付けており、あなたの参加を歓迎しています。

これはマイルストーンリリースです。開発者グループは安定して堅牢なメール/カレンダサーバの製品化へむけて大きな一
歩を踏み出し、すでに採用している組織がありますが、使用する際には厳しいテストを行うことを推奨します。POPおよび
SMTPは製品としての品質があると見なされ使用されていますが、IMAPおよびウェブメールはアルファまたはベータ段階のた
め、テストおよび開発としてデプロイすることを提案します。

インストールの過程でキーストアを生成することができますが、これは"自己サイン"キーです。これは、メールクライアン
トがキーを拒否するか、少なくてもキーが信頼された認証機関によって確認されていないとユーザに警告を出すことを意味
します。ThawteやVerisignまたはあなたの使用するメールクライアントが信頼するCAから認証を得ることを提案します。

また、インストール過程でHypersonicデータベースを使用した"デフォルト"データソースか、データソースを作成するかを
選択します。hypersonicの使用よりデータソースを作成することを強く推奨します。Hypersonicは小さなメールサーバと基
本的な設定のテストのみに適しています。インストーラはOracle、PostgreSQL、MySQL、Hypersonicの設定を行えますが、
JDBCドライバを持つ他のデータベースでも動作します。ただし、検証していません。インストーラはJDBCドライバ
（postgresqlまたはMySQL）をインストールできます。しかし、あなたの使用するドライバが含まれていない場合は、
$INSTALL/server/default/libディレクトリにドライバをコピーする必要があります（JBASに含まれているHypersonicは除
く）。ベンダからJDBCドライバをダウンロードし、上記のディレクトリに置くことを提案します。

インストール過程は、管理ツール
（http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#Configuring_Meldware）で編集でき
るデータベースユーザリポジトリを設定します。しかし、MCSは他のJBAS JAASユーザリポジトリもサポートします。設定例
はserver/default/deploy/mail.ear/mail.sar/META-INFディレクトリに含まれています。詳しくはwikiページ 
http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#JAASを参照してください。LDAPまたは
このリリース用のJAASを使用してセキュリティが確保されたデフォルトのデータベースの使用をすすめます。

== JMS ==

MCSはJava Messaging Serviceを使用します。JBoss App ServerのデフォルトのJMS設定は、Hypersonicを使用します。イン
ストーラはJMSが異なるデータベースを使用するよう再設定しません。HypersonicがJMSメッセージの本文をメモリへ読み出
すようにしたいかもしれません。

http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#Manually_Configuring_Datasources
http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#Configuring_JMS_Persistence

== APOP ==

このリリースは"APOP" (http://en.wikipedia.org/wiki/APOP)をサポートします。以前のバージョンとは異なりデフォルト
でグラフィカルインストーラが設定します。

=== あわてないでください ===
このreadmeの長さは、インストールが難しいと感じさせるかもしれません。GUIにそって、わからないことがあればいつでも
戻ってこの文章を読んでください。

=== 将来 ===

現在、設定はXMLが中心です。他のほとんどのメールサーバと比べ悪くはありませんが、グラフィカルなインタフェースより
易しくありません。1.0最終リリースではGUI設定を計画しています。

=== フィードバック ===

私たちはMCSとインストールへのあなたのフィードバックを本当に望んでいます。Buni.orgフォーラムへぜひご参加ください:
http://buni.org/forums/
そして、お気軽に質問してください。


Meldware Communication Suite開発者グループ
Buni.org
