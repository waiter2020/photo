package com.upc.photo;

import com.alibaba.fastjson.JSON;
import com.upc.photo.model.Share;
import com.upc.photo.service.impl.FaceGroupServiceImpl;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

/**
 * @Author: waiter
 * @Date: 2019/5/28 21:17
 * @Version 1.0
 */

public class Test {
    @org.junit.Test
    public void test(){
        Share share = new Share();
        share.setExpiration(135165L);
        ArrayList<BigInteger> bigIntegers = new ArrayList<>();
        bigIntegers.add(new BigInteger("84968464"));
        bigIntegers.add(new BigInteger("6565645"));
        // share.setShareList(bigIntegers);
        String s = JSON.toJSONString(share);
        System.out.println(s);
    }
    @org.junit.Test
    public void test2(){
        String s = "123456";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encode = encoder.encode(s.getBytes(StandardCharsets.UTF_8));
        System.out.println(new String(encode,StandardCharsets.UTF_8));
    }

    @org.junit.Test
    public void test3(){
        double[] a = {0.0350801, 0.0236022, 0.0613996, -0.0327737, 0.0264357, 0.0311307, -0.0423083, -0.0616561, -0.0291471, -0.0593094, -0.0287949, -0.00808112, -0.0351393, -0.00679112, 0.0320884, 0.0180344, 0.104544, 0.0320029, 0.0307425, 0.0630297, -0.0551475, -0.0533463, -0.0109648, -0.00279956, 0.0544429, -0.00218055, 0.0249028, 0.0393432, 0.0248643, 0.0697114, -0.0106519, 0.0248896, 0.0248831, 0.0882313, 0.0555727, 0.0581436, -0.0610868, 0.00955842, 0.0152376, 0.0275794, 0.00597623, -0.0284047, -0.0838166, -0.00711031, -0.0115036, 0.00457358, -0.0893765, -0.057866, 0.0170273, -0.0572457, -0.0227938, -0.0504745, -0.0216032, -0.0347608, 0.0597811, 0.0439211, 0.0388311, -0.0154324, -0.101225, -0.106938, -0.0380464, 0.00269973, -0.00562402, 0.0217524, 0.0440584, 0.0283224, -0.0921041, 0.0170191, 0.0665335, -0.0504706, 0.0039682, -0.0633872, -0.00188013, -0.0427152, -7.5479E-4, 0.0187502, -0.00233472, 0.00892464, -0.0106353, 0.0281738, -0.0259865, 0.0760549, -0.0309514, -0.0383648, -0.0386741, 0.0219185, 0.0750485, -0.0409494, -0.0553143, 0.0119802, -0.0286238, 0.0196011, -0.0681959, -0.097408, 0.0263754, 0.050693, -0.0396388, 0.0683328, -0.0514043, 0.0504181, -0.0191841, -0.00981277, 0.0251804, -0.0104679, 0.0260965, -0.0791092, -0.0082251, -0.0323794, -0.0322528, -0.0175917, -0.05548, -0.00643497, 0.0495681, -0.0531292, 0.0649096, 0.0265474, 0.0597988, -0.045182, -0.100344, -0.00203202, 0.0260744, 0.0384159, -0.0211629, 0.0561726, 7.11958E-4, -0.0401964, 0.00710471, -0.0395779, -0.0379639, -0.0864719, 0.0843565, 0.0575047, 0.0485531, -0.0405569, -0.0863931, 0.00467914, 0.0163037, -0.0254471, -0.0225958, -0.0355336, 0.0421721, -0.0208328, -0.0292747, -0.00345777, 0.0696988, -0.0213254, 0.056025, 0.0310047, -0.0520813, -0.0101335, -0.058701, 0.0298108, 0.00381576, 0.0401673, -0.0164727, -0.0186198, 0.0108436, 0.0284411, 0.00853561, -0.0112664, 0.0650092, 0.0526403, -0.104178, 0.0159793, 0.0849236, 0.113522, -0.0481944, -0.0049466, -0.0416532, 0.0308529, -0.00837314, 0.104029, 0.0676733, 0.0501338, -0.00815523, 0.00772166, 0.0336294, 0.0325255, -0.0117181, 0.0407636, 0.0419292, 0.0125569, 0.0506436, -0.0230041, -0.0101209, 0.0582608, 0.10093, -0.0393323, 0.00552914, 0.0208881, -0.0546205, -0.0308001, -0.0292669, 0.0803426, 0.0209033, 0.036235, -0.0258622, -0.0677763, 0.0509136, -0.0307007, -0.0320416, -0.0282722, 0.00364154, 0.0122131, 0.0165948, 0.00682319, -0.0402895, 0.0141013, 0.00766546, -0.1045, 0.0248267, 0.0239341, -0.123843, 0.0401587, -0.072052, 0.00293534, 0.0643541, -0.0934554, -0.0322961, 0.00249775, 0.00352158, -0.0868198, -0.010403, 0.00310075, 0.00220778, 0.017677, -0.0631054, 0.0323503, -0.0511311, -0.017593, -0.0128323, -0.0374117, 0.0321286, -0.0290395, -0.0333743, 0.0696544, -0.051187, -0.033289, -0.014723, -0.0117694, -0.0198238, 0.0655917, -0.0586499, -0.00536838, -0.0590243, 0.0129442, 0.0587234, 0.0543962, -0.0135306, 0.015481, 0.0910955, -0.0558421, 0.0344911, -0.0669037, 0.0603789, -0.0591193, -0.00206935, -0.0135406, 0.00489696, -0.0106921, 0.0590491, -0.0850794, 0.0754956, 0.067723, 0.0913351, 0.0376551, -0.00292305, 0.0661752, -0.032187, 0.0778766, -0.0167385, -0.0492838, -0.0267493, -0.0210765, 0.0478991, 0.0334026, -0.00648489, -0.0598901, 0.0107732, 0.0210157, 1.69423E-4, 0.0264218, 0.072286, 0.00321418, -0.0398005, -0.0285949, -0.018643, -0.075852, -0.023557, -0.01249, -0.0052545, 0.027302, 0.0236313, -0.037388, -0.0444688, -0.0025881, 0.00730541, -0.00307694, -0.0624874, -0.0325313, 0.0119329, 0.0579096, -0.0165571, -0.00409355, 0.0519698, -0.0740844, 0.0735816, -0.0250688, 0.0551878, -0.0629981, 0.00241068, 0.0555625, -8.91506E-5, 0.0657801, -0.0515018, 0.0425955, 0.0234547, -0.0172585, -0.0276718, 0.026806, 0.0195364, 0.0396461, 0.0681374, 0.0264118, 0.00431752, 0.0221571, 0.0548839, 0.0644583, -0.0126623, 0.0795881, 0.012462, 0.0359902, 0.0170102, -0.0317872, 0.0556276, 0.0424406, 0.00419437, -0.0601205, 0.0348259, -0.0173142, -0.042279, 0.0097454, -0.0706427, -0.0118366, -0.0444572, -0.047302, 0.0180853, 0.00675175, -0.0413967, -0.0272178, -0.00599853, -0.022675, -0.0308121, 0.0659501, 0.00649721, 0.0686789, -0.00356092, 0.0455811, 0.00646773, -0.0386934, 7.78002E-4, -0.00293199, -0.068713, -0.0235326, 0.0732164, 0.0267885, -0.0304191, 0.0367582, -0.00415654, -0.0701608, 0.0157015, -0.0267036, 0.0907727, 0.0855682, -0.0131718, -0.00642321, 0.0663995, 0.0236651, -0.0390524, -0.0531655, -0.0169373, -0.0201673, -0.0503795, 0.00977573, -0.023722, 6.53214E-4, -0.0297515, 0.106626, 0.0103983, -0.0705106, -0.00898748, 0.045619, -3.95444E-4, -0.017843, -0.0739963, 0.0124578, -0.0590645, 0.0400809, -0.0165252, 0.0706194, 0.0285504, -0.0423527, -0.00945323, 0.0209012, 0.0436926, -0.0326835, -0.100555, -0.00418001, 0.00718107, -0.0569164, 0.0395338, 0.0540266, -0.0108876, 0.0178214, -0.107065, 0.0245148, -0.0383519, -0.020022, -0.0207624, -0.00863878, 0.0168552, -0.0857049, -0.0233236, 0.00114926, -0.0275033, -0.108763, 0.0421804, -0.0514705, -0.0220636, -0.0338045, -0.0103142, -0.0372362, -0.0143921, -0.0356033, 0.0129052, 0.0314252, -0.0123911, -0.0226771, 0.0213661, -0.0897442, 0.0794091, 0.00694352, -4.88275E-5, -0.0110094, -0.0119249, 0.0251684, 0.0440993, -0.0122086, -0.0356902, -0.0238582, -0.036778, 0.0120166, -0.0157754, -0.0369196, 0.0301746, 0.0186348, -0.0653232, 0.0565001, 0.0112363, 0.0517897, 0.0334822, 0.0441113, 0.0139651, -0.0393787, 0.0370619, 0.0408013, 0.0264933, -0.0459793, -0.0703529, 0.0652322, -0.0251933, -0.0289346, 0.0162418, -0.0285476, -0.0877523, -0.00444051, 0.00159094, -0.0349512, -0.0351483, 0.00663205, 0.0371623, -0.00963213, 0.00824931, 0.0469955, 0.0872323, -0.0429768, -0.0141901, 0.0044122, -0.0524383, 0.00341193, -0.037509, -0.0910563, 0.0264374, 0.0450392, 0.015762, 0.0667744, -0.0130634, -0.0181161, -0.0331029, 3.93872E-5, 0.0060739, -0.0387211, 0.0299396, 0.0207201, -0.00843489, -0.00151735, -0.07452, 0.00241174, -0.062181, 0.0274172, -0.0176145, 0.00635232};
        System.out.println(a.length);


    }
    private double getDist(RealMatrix matrix1,RealMatrix matrix2){
        RealMatrix subtract = matrix1.subtract(matrix2);
        double[][] data = subtract.getData();
        double sum = 0;
        for (double[] d : data) {
            for (double dd : d) {
                sum+=Math.pow(dd,2);
            }
        }
        return Math.sqrt(sum);
    }
}