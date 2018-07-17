from skimage import io,transform
import tensorflow as tf
import numpy as np
flower_dict = {\
    2:'雏菊（学名：Bellis perennis L.），又名马头兰花、延命菊，春菊、太阳菊等，早春开花，生气盎然，具有君子的风度和天真烂漫的风采，深得意大利人的喜爱，因而推举为国花。',\
    3:'蒲公英（拉丁学名：Taraxacum mongolicum Hand.-Mazz.）,别名黄花地丁、婆婆丁、华花郎等。种子上有白色冠毛结成的绒球，花开后随风飘到新的地方孕育新生命。',\
    1:'玫瑰（学名：Rosa rugosa Thunb.）：原产地中国。属蔷薇目，蔷薇科落叶灌木，玫瑰是英国的国花。通俗意义中的“玫瑰”已成为多种蔷薇属植物的通称。',\
    4:'向日葵（拉丁文：Helianthus annuusL.），是菊科向日葵属的一年生草本植物。原产南美洲，驯化种由西班牙人于1510年从北美带到欧洲，最初为观赏用。',\
    0:'郁金香（学名：Tulipa gesneriana L.）是百合科郁金香属的多年生草本植物，具球茎。郁金香被广泛认为原产于土耳其，是土耳其、荷兰、匈牙利等国的国花。'}

def read_one_image(path):
    w=100
    h=100
    c=3
    img = io.imread(path)
    img = transform.resize(img,(w,h),mode='constant')
    return np.asarray(img)
def classification(path):
    with tf.Session() as sess:
        data = []
        data1 = read_one_image(path)
        data.append(data1)

        saver = tf.train.import_meta_graph('./flowerModal/model.ckpt.meta')
        saver.restore(sess,tf.train.latest_checkpoint('./flowerModal/'))

        graph = tf.get_default_graph()
        x = graph.get_tensor_by_name("x:0")
        feed_dict = {x:data}

        logits = graph.get_tensor_by_name("logits_eval:0")

        classification_result = sess.run(logits,feed_dict)
        output = []
        output = tf.argmax(classification_result,1).eval()
        return flower_dict[output[0 ]]
