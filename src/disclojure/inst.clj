(ns disclojure.inst
  (:require [overtone.live :refer :all]))

(definst plucky [freq 440 dur 1 amp 1 cutoff 3000 fil-dur 0.1]
         (let [env (env-gen (asr 0 1 1) (line:kr 1.0 0.0 dur) :action FREE)
               level (+ (* 0.85 freq) (env-gen (perc 0 fil-dur) :level-scale cutoff))]
           (-> (pulse freq)
               (lpf level)
               (free-verb :room 1 :mix 0.45)
               (* env amp)
               pan2)))

(definst supersaw [freq 440 dur 0.2 release 0.5 amp 0.6 cutoff 3500 env-amount 0.5]
         (let [snd-fn (fn [freq]
                        (let [tune (ranged-rand 0.99 1.01)]
                          (-> (lf-saw (* freq tune))
                              (delay-c 0.005 (ranged-rand 0.0001 0.01)))))
               hi-saws (splay (repeatedly 7 #(snd-fn freq)))
               lo-saws (splay (repeatedly 5 #(snd-fn (/ freq 2))))
               noise (pink-noise)
               snd (+ (* 0.65 hi-saws) (* 0.85 lo-saws) (* 0.12 noise))
               env (env-gen (adsr 0.001 0.7 0.2 0.1) (line:kr 1 0 (+ dur release)) :action FREE)]
           (-> snd
               (clip2 0.45)
               (rlpf (+ freq (env-gen (adsr 0.001) (line:kr 1 0 dur) :level-scale cutoff)) 0.75)
               (free-verb :room 1.8 :mix 0.45)
               (* env amp)
               pan2)))

(definst stab [freq 440 detune 0.35 dur 0.2 amp 0.6 cutoff 950]
         (let [freqs (repeat 5 freq)
               freqs (map #(+ (* detune (rand)) %) freqs)
               saws (splay (lf-saw freqs))
               snd (* 0.65 saws)
               env (env-gen (adsr 0.01 0.1 0.8 0.4) (line:kr 1 0 (* dur 1/2)) :action FREE)]
           (-> snd
               (rhpf (+ (* freq 2) cutoff) 0.25)
               (* env amp)
               pan2)))

(definst bass [freq 220 dur 1.0 amp 0.3 osc-mix 0.5 cutoff 1500]
         (let [sub-freq (/ freq 2)
               osc1 (lf-saw freq)
               osc2 (pulse sub-freq 0.51)
               snd (+ (* osc-mix osc2) (* (- 1 osc-mix) osc1))
               level (+ (/ freq 4) (env-gen (perc 0 dur) :level-scale cutoff))
               snd (rlpf snd level 0.7)
               env (env-gen (adsr 0.0001) (line:kr 1.0 0.0 dur) :action FREE)]
           (pan2 (* amp env snd))))
